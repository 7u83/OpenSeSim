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

/**
 *
 * @author tube
 */
public class Position {

    final Exchange se;
    final Account account;

    long shares;
    long entryPrice;
    long margin;
    private boolean isShort;
    long borrowed = 0;

    public Position(Exchange se, Account account) {
        this.se = se;

        shares = 0;
        entryPrice = 0;
        margin = 0;
        this.account = account;
    }

    public String getName() {
        return se.getSymbol();
    }

    public float getShares() {
        return shares / se.shares_df;
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
        return getMargin_Long() / se.money_df;
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

        return currentPrice * shares - shadow_cash;

        //long diff = currentPrice - entryPrice;
        //return isShort ? -shares * diff : shares * diff;
    }

    public float getPnL() {
        return (se.getLastPrice_Long() * shares - totalEntryCost) / se.money_df;
    }

    public float getPnLPercent() {
        float base;

        if (getMargin() != 0) {
            // gehebelter Trade → Prozent relativ zur eingesetzten Margin
            base = getMargin();
        } else {
            // ungehebelter Trade → Prozent relativ zu den gesamten Entry-Kosten
            base = getTotalEntryCost();
            if (base == 0) {
                return 0;
            }
        }

        return (getPnL() / base) * 100.0f;
    }

    public float getTotalEntryCost() {
        return totalEntryCost / se.money_df;
    }

    long shadow_cash = 0;
    long totalEntryCost = 0;

    public float getShadowCash() {
        return shadow_cash / se.money_df;
    }

    public float getNetBrokerLoan() {
        return shadow_cash / se.money_df;
    }
    public boolean mops = true;

    void addShares(long volume, long price, int leverage) {
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = volume * price;
            shadow_cash -= val;
            totalEntryCost += val;

            // Führt zu Zukauf (Long->Long oder Short->Short).
            // Hier muss die Initial Margin des neuen Trades hinzugefügt werden.
            long marginRequired = Math.abs(val / leverage);

            shares += volume;
            margin += marginRequired;
            account.cash -= marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares)) {
                // close old position

                shadow_cash -= (-shares * price);
             //   totalEntryCost += (-shares * price);

                account.cash += shadow_cash + margin;
                shares = nextShares;

                long val = shares * price;
                shadow_cash = -val;
                totalEntryCost = val;

                // 2. Neue Margin für den "Überhang" berechnen
                long marginRequired = Math.abs(val) / leverage;

                account.cash -= marginRequired;
                margin = marginRequired;

            } // B. Positionsreduzierung (Teilverkauf/Rückkauf: Vorzeichen bleibt gleich)
            else {
                long val = volume * price;
                shadow_cash -= val;
                totalEntryCost += val;

                // Hier ist Ihr Prinzip der anteiligen Reduzierung korrekt.
                // Die Margin muss proportional zum geschlossenen Teil reduziert werden.
                // Anteil des geschlossenen Teils: |volume| / |sharesBefore| (mit 1000er Faktor)
                long reductionFactor = Math.abs(volume) * 10000 / Math.abs(shares);
                long marginReduction = (margin * reductionFactor) / 10000;
                shares = nextShares;
                margin -= marginReduction; // Reduziere die aggregierte Margin
                account.cash += marginReduction;   // Freigegebene Margin zurück zu Cash
            }
        }

        if (shares == 0 || (shares > 0 && shadow_cash + margin >= 0)) {

            //cash += margin;
            account.cash += shadow_cash + margin;
            //     cash+=Math.abs(margin);
            margin = 0;
            shadow_cash = 0;
            if (shares==0)
                totalEntryCost=0;
            return;
        }

    }
    
    public long getRequiredCashForOrder(long volume, long price, long leverage){
        if (Long.signum(shares) == Long.signum(volume) || shares == 0) {

            long val = volume * price;
            long marginRequired = Math.abs(val / leverage);
            return marginRequired; // Ziehe die benötigte Initial Margin vom Cash ab

        } // 2. Positionsverringerung/Umkehrung (Verkauf/Rückkauf: Vorzeichen sind gegensätzlich)
        else {

            long nextShares = shares + volume;

            // A. Positionsumkehr (Nulldurchlauf): sharesAfter hat ein anderes 
            // Vorzeichen als sharesBefore.
            if (Long.signum(shares) != Long.signum(nextShares) && nextShares!=0) {
                long val = nextShares * price;
                long marginRequired = Math.abs(val) / leverage;
                return  marginRequired;


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
    
}
