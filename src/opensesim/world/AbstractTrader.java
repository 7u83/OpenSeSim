/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.world;


import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class AbstractTrader implements Trader {

    private String name = "Unnamed";
    private String status;
    private World world;
    protected Account account;
    
    /**
     * @return the world
     */
    public final World getWorld() {
        return world;
    }

    /**
     * @param world the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    private boolean verbose=false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    

    /**
     * @return the status
     */
    @Override
    public String getStatus() {
        return status;
    }

    
    /**
     * @param status the status to set
     */
    protected void setStatus(String status) {
        this.status = status;
        if (verbose){
            System.out.printf("STA: %s: %s\n",this.getName(),this.getStatus());
        }

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    protected void setName(String name) {
        this.name = name;
    }

    public AbstractTrader(World world, JSONObject cfg) {
        this.world=world;
        this.account = new Account();
        
        AssetPack pack;
        pack = new AssetPack(this.world.getDefaultAssetPair().getCurrency(),1000);
        this.account.add(pack);
        pack = new AssetPack(this.world.getDefaultAssetPair().getAsset(),2000);
        this.account.add(pack);        
    }

    protected void log(String s){

        if (verbose){
            System.out.printf("LOG: %s: %s\n",this.getName(),s);
        }
        
    }

    @Override
    public Account getAccount(){
        return account;
    }
}
