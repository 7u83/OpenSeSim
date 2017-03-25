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
package sesim;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MinMax {

    protected float min;
    protected float max;
    protected float min_log;
    protected float max_log;    
    
    private boolean log;

    MinMax(float min, float max) {
        this.min = min;
        this.max = max;
        this.log = false;
    }

    public float getDiff() {
        return !log ? max - min : max_log - min_log;
    }

    public float getMin() {
        return !log ? min : min_log;
    }
    
    public float getMin(boolean plog) {
        return !plog ? min : min_log;
    }
    

    public float getMax() {
        return !log ? max : max_log;
    }

    public float getMax(boolean plog) {
        return !plog ? max : max_log;
    }
    
    
    public void setLog(boolean log){
        min_log = (float) Math.log(min);
        max_log = (float) Math.log(max);
        this.log=log;
    }
    
    public void setMin(float min){
        this.min=min;
    }
    
    public void setMax(float max){
        this.max=max;
    }

    public boolean isLog(){
        return log;
    }
    
}
