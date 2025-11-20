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

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author tube
 */
public class Position {
    private static final AtomicLong ID_GEN = new AtomicLong(0);
    final Asset asset;
    final Account account;

    long shares;
    long entryPrice;
    long margin;
    private boolean isShort;
    long borrowed = 0;
    private long stopPrice;
    long id;

    public Position(Asset asset, Account account) {
        this.asset = asset;
        id = ID_GEN.incrementAndGet();

        shares = 0;
        entryPrice = 0;
        margin = 0;
        this.account = account;
    }

    public String getName() {
        return asset.getSymbol();
    }

    public long getShares_Long() {
        return shares;
    }

    public float getShares() {
        return shares / asset.getDf();
    }

    public float getLeverage() {

        float leverage;
        if (getMargin() == 0) {
            leverage = 1;
        } else {
            leverage = getTotalEntryCost() / getMargin();
        }
        return leverage;
    }

    public float getMargin() {
        return getMargin_Long() / asset.getMarket().money_df;
    }

    public long getMargin_Long() {
        return Math.abs(margin);
    }

    public float getEntryPrice() {
        return this.getTotalEntryCost() / shares;
    }

    // Exposure = total Position value
    public long getExposure() {
        return Math.abs(shares * entryPrice);
    }

    // unrealized PnL für aktuelle Preis
    public long getPnL_Long(long currentPrice) {

        return currentPrice * shares - netCashFlow;

        //long diff = currentPrice - entryPrice;
        //return isShort ? -shares * diff : shares * diff;
    }

    public long getPnL_Long() {
        return asset.getMarket().getLastPrice_Long() * shares + netCashFlow;
    }

    public float getPnL() {
        return (asset.getMarket().getLastPrice_Long() * shares + netCashFlow) / asset.getMarket().money_df;
    }

    public float getPnLPercent() {
        float base;

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

    public long getMarketValue_Long() {
        return totalEntryCost - asset.getMarket().getLastPrice_Long() * shares - netCashFlow;
    }

    public float getMarketValue() {
        return getMarketValue_Long() / asset.getMarket().money_df;
    }

    public long getEquityValue_Long() {
        //long mypnl = pnl;
        
        return netCashFlow + shares * asset.getMarket().getLastPrice_Long();
//        mypnl-=val;
        
    //    if (margin == 0) {
     //return totalEntryCost - se.getMarket().getLastPrice_Long() * shares - pnl;
  //  return mypnl;
         //   return se.getMarket().getLastPrice_Long() * shares - pnl;
            //return getMarketValue_Long();
   //     }
     //   return  margin+getPnL_Long();
    }

    public float getEquityValue() {

        return getEquityValue_Long() / asset.getMarket().money_df;
    }

    public float getTotalEntryCost() {
        return totalEntryCost / asset.getMarket().money_df;
    }

    long netCashFlow = 0;
    long totalEntryCost = 0;

    public float getShadowCash() {
        return netCashFlow / asset.getMarket().money_df;
    }

    public float getNetBrokerLoan() {
        return netCashFlow / asset.getMarket().money_df;
    }
    public boolean mops = true;

    boolean isShort() {
        return shares < 0;
    }

    void addShares(long volume, long price, int leverage) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = volume * price;
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

                netCashFlow += shares * price;
                //   totalEntryCost += (-shares * price);

                account.cash += netCashFlow; // + margin;
                shares = nextShares;

                long val = shares * price;
                netCashFlow = -val;
                totalEntryCost = val;

                // 2. Neue Margin für den "Überhang" berechnen
                long marginRequired = Math.abs(val) / leverage;

                //      account.cash -= marginRequired;
                margin = marginRequired;

            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
            else {
                long val = volume * price;
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

        if (shares == 0 || (shares > 0 && netCashFlow + margin >= 0)) {

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
        }else{
                       asset.getMarket().removeLiquidationStop(this);
        }

    }
    
    public float getStopPrice(){
        return this.stopPrice/asset.getMarket().money_df;
    }
    
    public long getStopPrice_Long(){
        return this.stopPrice;
    }
    
    void setStopPrice(long newStopPrice){

            asset.getMarket().removeLiquidationStop(this);

        stopPrice=newStopPrice;
        asset.getMarket().setLiquidationStop(this);
    }
    
 

    public long getRequiredCashForOrder_Long(long volume, long price, long leverage) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = volume * price;
            long marginRequired = Math.abs(val / leverage);
            return marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares) && nextShares != 0) {
                long val = nextShares * price;
                long marginRequired = Math.abs(val) / leverage;
                return marginRequired;

            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
            else {
                return 0;
                /*   long val = volume * price;

                long reductionFactor = Math.abs(volume) * 10000 / Math.abs(shares);
                long marginReduction = (margin * reductionFactor) / 10000;
                return marginReduction;   // Freigegebene Margin zurück zu Cash*/
            }
        }
    }

    public long getTradableShares_Long(long volume, long price, long leverage) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = volume * price;
            long marginRequired = Math.abs(val / leverage);

            if (account.getFreeMargin_Long() < marginRequired) {
                return account.getFreeMargin_Long() * leverage / price;
            }

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares) && nextShares != 0) {

                long c = netCashFlow - (-shares * price) + margin + account.cash;

                long val = nextShares * price;
                long marginRequired = Math.abs(val) / leverage;
                if (c < marginRequired) {
                    return Math.abs(shares) + (c) * leverage / price;
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
            asset.getMarket().cancelOrder(account, this.liquidationOrder.id);
            this.liquidationOrder = null;
            return;
        }

        long liquidationPrice;

        int leverage = (int) (totalEntryCost / margin);

        if (liquidationOrder != null) {
            asset.getMarket().cancelOrder(account, liquidationOrder.id);
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
