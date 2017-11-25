/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
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
package indicators;

import org.json.JSONObject;
import sesim.OHLCData;
import sesim.OHLCDataItem;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SMAIndicator extends BaseIndicator {

    private OHLCData parent;

    OHLCData indicator;

    public SMAIndicator() {
        indicator = new OHLCData();
    }

    public void setParent(OHLCData parent) {
        this.parent = parent;
    }

    final String LEN = "len";
    int len = 60;

    @Override
    public void putConfig(JSONObject cfg) {

        len = cfg.getInt(LEN);

    }

    @Override
    public JSONObject getConfig() {
        JSONObject r;
        r = new JSONObject();
        r.put(LEN, len);
        return r;
    }

    private float getAt(int pos) {
        if (parent.size() == 0) {
            return 0;
        }

        int start = pos - len;
        if (start < 0) {
            start = 0;
        }
        float sum = 0;
        for (int i = start; i <= pos; i++) {
            sum += parent.get(i).close;
        }

        return sum / (pos - start + 1);
    }

    public void update() {

        if (parent.size() == 0) {

            return;
        }

        if (parent.size() == indicator.size()) {
            int i = parent.size() - 1;
            OHLCDataItem p = parent.get(i);
            float pr = this.getAt(i);
            OHLCDataItem it = new sesim.OHLCDataItem(p.time, pr, 0);
            this.indicator.set(i, it);
            return;
        }

        for (int i = indicator.size(); i < parent.size(); i++) {
            OHLCDataItem p = parent.get(i);
            float pr = this.getAt(i);
            OHLCDataItem it = new sesim.OHLCDataItem(p.time, pr, 0);

            this.indicator.add(it);

        }
    }

    public OHLCData getData() {
        update();
        return indicator;

    }

}
