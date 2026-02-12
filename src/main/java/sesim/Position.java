/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package sesim;

import sesim.util.FixedPoint;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author tube
 */
public class Position {

    private static final AtomicLong ID_GEN = new AtomicLong(0);
    final Market market;
    final Account account;

    long shares;
    long entryPrice;
    long margin;
    //  private boolean isShort;
    // long borrowed = 0;
    private long stopPrice;
    long id;

    public Position(Market asset, Account account) {
        this.market = asset;
        id = ID_GEN.incrementAndGet();

        shares = 0;
        entryPrice = 0;
        margin = 0;
        this.account = account;
    }

    public Position(Position p) {
        this.market = p.market;
        this.id = p.id;
        this.account = p.account;
        this.margin = p.margin;
        this.netCashFlow = p.netCashFlow;
        this.shares = p.shares;
    }

    public String getName() {
        return market.getAsset().getSymbol();
    }

    public long getShares_Long() {
        return shares;
    }

    public double getShares() {
        return FixedPoint.toExternal(getShares_Long());
    }

    public double getLeverage() {

        double leverage;
        if (getMargin() == 0) {
            leverage = 1;
        } else {
            leverage = getTotalEntryCost() / getMargin();
        }
        return leverage;
    }

    public double getMargin() {
        return FixedPoint.toExternal(getMargin_Long()); // / account.currency.getDf();
    }

    public long getMargin_Long() {
        return Math.abs(margin);
    }

    public double getEntryPrice() {
        return this.getTotalEntryCost() / shares;
    }

    // Exposure = total Position value
/*    public long getExposure() {
        return Math.abs(shares * entryPrice);
    }
     */
    // unrealized PnL für aktuelle Preis
    public long getPnL_Long(long currentPrice) {

        return FixedPoint.multiply(currentPrice, shares) - netCashFlow;

        //long diff = currentPrice - entryPrice;
        //return isShort ? -shares * diff : shares * diff;
    }

    public long getPnL_Long() {
        return getPnL_Long(market.getLastPrice_Long());
        //market.getLastPrice_Long() * shares + netCashFlow;
    }

    public double getPnL() {
        return FixedPoint.multiply(market.getLastPrice_Long(), shares)
                + netCashFlow;  // market.currency.getDf();

        //return (market.getLastPrice_Long() * shares + netCashFlow) / market.currency.getDf();
    }

    public double getPnLPercent() {
        double base;

        if (getMargin() != 0) {
            // gehebelter Trade → Prozent relativ zur eingesetzten Margin
            base = getMargin();
        } else {
            // ungehebelter Trade → Prozent relativ zu den gesamten Entry-Kosten
            base = -netCashFlow;
            if (base == 0) {
                return 0;
            }
        }

        return (getPnL() / base) * 100.0f;
    }

    /**
     * Get the market value of the position
     *
     * @return market value in units
     */
    public long getMarketValue_Long() {
        return FixedPoint.multiply(market.getLastPrice_Long(), shares);
    }

    public double getMarketValue() {
        return FixedPoint.toExternal(getMarketValue_Long());
    }

    public long getEquityValue_Long(long price) {
        return netCashFlow + FixedPoint.multiply(shares, price); //asset.getMarket().getLastPrice_Long();
    }

    public long getEquityValue_Long() {
        return getEquityValue_Long(market.getLastPrice_Long());
    }

    public double getEquityValue() {

        return FixedPoint.toExternal(getEquityValue_Long());

//getEquityValue_Long() / market.currency.getDf();
    }

    public double getTotalEntryCost() {
        return FixedPoint.toExternal(totalEntryCost); // / market.currency.getDf();
    }

    long netCashFlow = 0;
    long totalEntryCost = 0;

    public double getNetCashFlow() {
        return FixedPoint.toExternal(netCashFlow); // / market.currency.getDf();
    }

    /*    public float getNetBrokerLoan() {
        return netCashFlow / market.getMarket().money_df;
    }*/
    public boolean mops = true;

    boolean isShort() {
        return shares < 0;
    }

    void addShares(double volume, double price, int leverage) {
        addShares_Long(
                market.asset.round_Long(FixedPoint.toInternal(volume)),
                market.currency.round_Long(FixedPoint.toInternal(price)),
                leverage
        );
    }

    void addShares_Long(long volume, long price, int leverage) {
        if (account.maxMargin==0){
            // traditional trading, just buy/sell with 100% coverage 
            
            long val = FixedPoint.floorMultiply(volume, price);
            shares+=volume;
            account.cash-=val;
            return;
        }
        
        
        

        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = FixedPoint.floorMultiply(volume, price);
            netCashFlow -= val;
            totalEntryCost += val;

            // Führt zu Zukauf (Long->Long oder Short->Short).
            // Hier muss die Initial Margin des neuen Trades hinzugefügt werden.
            long marginRequired = Math.abs(val / leverage);

            shares += volume;
            margin += marginRequired;
            //    account.cash -= marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares)) {
                // close old position

                netCashFlow += FixedPoint.floorMultiply(shares, price); //shares * price;
                //   totalEntryCost += (-shares * price);

                account.cash += netCashFlow; // + margin;
                shares = nextShares;

                long val = FixedPoint.floorMultiply(shares, price);
                netCashFlow = -val;
                totalEntryCost = val;

                // 2. Neue Margin für den "Überhang" berechnen
                long marginRequired = Math.abs(val) / leverage;

                //      account.cash -= marginRequired;
                margin = marginRequired;

            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
            else {
                long val = FixedPoint.floorMultiply(volume, price);
                netCashFlow -= val;
                totalEntryCost += val;

                // Hier ist Ihr Prinzip der anteiligen Reduzierung korrekt.
                // Die Margin muss proportional zum geschlossenen Teil reduziert werden.
                // Anteil des geschlossenen Teils: |volume| / |sharesBefore| (mit 1000er Faktor)
                long reductionFactor = Math.abs(volume) * 10000 / Math.abs(shares);
                long marginReduction = (margin * reductionFactor) / 10000;
                shares = nextShares;
                margin -= marginReduction; // Reduziere die aggregierte Margin
                //    account.cash += marginReduction;   // Freigegebene Margin zurück zu Cash
            }
        }

/*        if (shares == 0 || (shares > 0 && netCashFlow + margin >= 0 && account.cash + netCashFlow >= 0)) {

            //cash += margin;
            account.cash += netCashFlow; // + margin;
            //     cash+=Math.abs(margin);
            margin = 0;
            netCashFlow = 0;
            if (shares == 0) {
                totalEntryCost = 0;
            }

        }*/

        if (this.margin != 0) {
            this.account.calculateLiquidationStops(price);
        } else {
            market.removeLiquidationStop(this);
        }

    }

    /*   
    this is the very general add shares funtion which handles both
    leveraged and noh leveraged orders. But its to complex.
    
    void addShares_Long(long volume, long price, int leverage) {
        
        
        
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = FixedPoint.floorMultiply(volume, price);
            netCashFlow -= val;
            totalEntryCost += val;

            // Führt zu Zukauf (Long->Long oder Short->Short).
            // Hier muss die Initial Margin des neuen Trades hinzugefügt werden.
            long marginRequired = Math.abs(val / leverage);

            shares += volume;
            margin += marginRequired;
            //    account.cash -= marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares)) {
                // close old position

                netCashFlow += FixedPoint.floorMultiply(shares, price); //shares * price;
                //   totalEntryCost += (-shares * price);

                account.cash += netCashFlow; // + margin;
                shares = nextShares;

                long val = FixedPoint.floorMultiply(shares, price);
                netCashFlow = -val;
                totalEntryCost = val;

                // 2. Neue Margin für den "Überhang" berechnen
                long marginRequired = Math.abs(val) / leverage;

                //      account.cash -= marginRequired;
                margin = marginRequired;

            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
            else {
                long val = FixedPoint.floorMultiply(volume, price);
                netCashFlow -= val;
                totalEntryCost += val;

                // Hier ist Ihr Prinzip der anteiligen Reduzierung korrekt.
                // Die Margin muss proportional zum geschlossenen Teil reduziert werden.
                // Anteil des geschlossenen Teils: |volume| / |sharesBefore| (mit 1000er Faktor)
                long reductionFactor = Math.abs(volume) * 10000 / Math.abs(shares);
                long marginReduction = (margin * reductionFactor) / 10000;
                shares = nextShares;
                margin -= marginReduction; // Reduziere die aggregierte Margin
                //    account.cash += marginReduction;   // Freigegebene Margin zurück zu Cash
            }
        }

        if (shares == 0 || (shares > 0 && netCashFlow + margin >= 0 && account.cash + netCashFlow >= 0)) {

            //cash += margin;
            account.cash += netCashFlow; // + margin;
            //     cash+=Math.abs(margin);
            margin = 0;
            netCashFlow = 0;
            if (shares == 0) {
                totalEntryCost = 0;
            }

        }

        if (this.margin != 0) {
            this.account.calculateLiquidationStops(price);
        } else {
            market.removeLiquidationStop(this);
        }

    }*/

 /*
    private void closePosition(long currentPrice) {
        long positionValue = FixedPoint.floorMultiply(this.shares, currentPrice);

        // Die Rückrechnung ist immer gleich: Wert + Flow + Margin
        account.cash += (positionValue + netCashFlow + margin);

        this.shares = 0;
        this.margin = 0;
        this.netCashFlow = 0;
    }

    void addShares_Long(long volume, long price, int leverage) {
        if (volume == 0) {
            return;
        }

        // FALL 1: Zukauf / Eröffnung
        if (shares == 0 || Long.signum(shares) == Long.signum(volume)) {
            long totalVal = FixedPoint.floorMultiply(volume, price);
            long absVal = Math.abs(totalVal);

            // 1. Wie viel Cash können/müssen wir nehmen?
            long requiredCash = absVal / leverage;

            // WICHTIG: Niemals mehr nehmen als da ist -> verhindert account.cash < 0
            long cashToTake = Math.min(account.cash, requiredCash);

            account.cash -= cashToTake;

            // 2. Verbuchung je nach Hebel-Typ
            if (leverage == 1) {
                // SPEZIALFALL HEBEL 1: Der User will margin == 0 sehen.
                // Wir regeln alles über den Flow.

                if (volume > 0) { // LONG
                    // Wenn wir zu wenig Cash hatten (z.B. Mischkalkulation),
                    // erhöhen wir die "Schuld" im Flow um den fehlenden Betrag.
                    // Flow -= (Wert - Gezahltes)
                    this.netCashFlow -= (absVal - cashToTake);
                } else { // SHORT
                    // Bei Short Hebel 1: Wir haben Cash hinterlegt (cashToTake) 
                    // UND den Verkaufserlös (absVal) erhalten. Beides muss zurückkommen.
                    // Da Margin 0 sein muss, packen wir beides in den Flow.
                    this.netCashFlow += (absVal + cashToTake);
                }
                // Margin bleibt unangetastet (0)!

            } else {
                // STANDARD MARGIN TRADING (Hebel > 1)
                this.margin += cashToTake;
                this.netCashFlow -= totalVal; // Long: -Wert, Short: +Wert
            }

            this.shares += volume;
        } // FALL 2: Umkehr (Flip)
        else if (Math.abs(volume) >= Math.abs(shares)) {
            long remaining = volume + shares; // shares hat anderes Vorzeichen
            closePosition(price);
            if (remaining != 0) {
                addShares_Long(remaining, price, leverage);
            }
        } // FALL 3: Teil-Schließung
        else {
            long factor = Math.abs(volume) * 10000 / Math.abs(shares);
            long mRelease = (margin * factor) / 10000;
            long fRelease = (netCashFlow * factor) / 10000;
            long saleValue = FixedPoint.floorMultiply(-volume, price);

            account.cash += (saleValue + fRelease + mRelease);
            this.margin -= mRelease;
            this.netCashFlow -= fRelease;
            this.shares += volume;
        }
    }*/
    private void updateLiquidation(long price) {
        if (this.margin != 0 || this.netCashFlow != 0) {
            this.account.calculateLiquidationStops(price);
        } else {
            market.removeLiquidationStop(this);
        }
    }

    public double getStopPrice() {
        return FixedPoint.toExternal(this.stopPrice);
    }

    public long getStopPrice_Long() {
        return this.stopPrice;
    }

    void setStopPrice(long newStopPrice) {

        market.removeLiquidationStop(this);

        stopPrice = newStopPrice;
        //System.out.printf("Stop for %s, %d\n", this.account.getOwner().getName(),stopPrice);
        market.setLiquidationStop(this);
    }

    long getSharesToTrade(long volume) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {
            return volume;
        }
        return shares + volume;
    }

    public long getRequiredCashForOrder_Long(long volume, long price, long leverage) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = FixedPoint.multiply(volume, price);
            long marginRequired = Math.abs(val / leverage);
            return marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } else {

            long nextShares = shares + volume;

            if (Long.signum(shares) != Long.signum(nextShares) && nextShares != 0) {
                long val = FixedPoint.multiply(nextShares, price);
                long marginRequired = Math.abs(val) / leverage;
                return marginRequired;

            } else {
                return 0;

            }
        }
    }

    public long getTradableShares_Long(long volume, long price, long leverage) {
        // account w/o margin (simple)
        if (account.maxMargin==0){
            if (volume<0){
                
                if (shares+volume<=0){
                    return shares;
                }
                return Math.abs(volume);
            }
            long vmax = FixedPoint.floorDivide(account.cash,price);
            if (vmax<volume){
                return market.getAsset().round_Long(vmax);
            }
            return volume;
        }
        
        
        // account w/ margin (complex)
      
        
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {
            // Zukauf/Zuverkauf
            
            long freeMargin = account.getFreeMargin_Long();
            if (freeMargin <= 0) {
                return 0;
                //freeMargin = 0;
            }

            long val = FixedPoint.floorMultiply(volume, price);
            long marginRequired = Math.abs(val / leverage);


            if (freeMargin < marginRequired) {

                return this.market.getAsset().round_Long(
                        FixedPoint.floorDivide(freeMargin * leverage, price));

            }
            

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares) && nextShares != 0) {

                long c = netCashFlow - FixedPoint.multiply(-shares, price) + margin + account.cash;

                long val = FixedPoint.multiply(nextShares, price);
                long marginRequired = Math.abs(val) / leverage;
                if (c < marginRequired) {
                    return Math.abs(shares) + FixedPoint.divide((c) * leverage, price);
                }

                //return marginRequired;
            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)

        }
        return Math.abs(volume);
    }

    Order liquidationOrder = null;

    void updateLiquidationOrder(int l) {
        if (margin == 0) {
            if (liquidationOrder == null) {
                return;
            }
            market.cancelOrder(account, this.liquidationOrder.id);
            this.liquidationOrder = null;
            return;
        }

        long liquidationPrice;

        int leverage = (int) (totalEntryCost / margin);

        if (liquidationOrder != null) {
            market.cancelOrder(account, liquidationOrder.id);
        }

        /*    if (shares > 0) {
            liquidationPrice = (margin * leverage - margin) / Math.abs(shares);
            this.liquidationOrder
                    = se.getExchange().createOrderNoExec_Long(account, (byte) (Order.SELL | Order.STOP),
                            Math.abs(shares), 0, liquidationPrice, leverage);
        } else {
            liquidationPrice = (margin * leverage + margin) / Math.abs(shares);
            this.liquidationOrder
                    = se.getExchange().createOrderNoExec_Long(account, (byte) (Order.BUY | Order.STOP),
                            Math.abs(shares), 0, liquidationPrice, leverage);
        }*/
    }

}
