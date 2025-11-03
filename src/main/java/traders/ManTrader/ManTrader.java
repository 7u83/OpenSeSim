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
package traders.ManTrader;

import gui.Globals;
import gui.OpenOrdersList;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import sesim.Scheduler.Event;
import org.json.JSONObject;
import sesim.Account;

import sesim.AutoTraderBase;

//import sesim.AutoTraderConfig;
import sesim.AutoTraderGui;
import sesim.AutoTraderInterface;
import sesim.Exchange;
import sesim.Exchange.AccountListener;
import sesim.Order;
import sesim.Sim;

import java.io.File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class ManTrader extends AutoTraderBase implements AccountListener, AutoTraderInterface {

    String soundFile = null;
    int soundVolume = 50;

//    public ManTrader(Exchange se, long id, String name, float money, float shares, AutoTraderConfig config) {
//        //  super(se, id, name, money, shares, null);
//        super();
//    }
    public final ConcurrentHashMap<Long, Order> allOrders = new ConcurrentHashMap<>();

    public ManTrader() {
        super();

    }

    @Override
    public void init(Sim sim, long id, String name, float money, float shares, String strat, JSONObject cfg) {
        super.init(sim, id, name, money, shares, strat, cfg);
        getAccount().setListener(this);
    }
    ManTraderConsoleDialog consoleDialog=null;

    @Override
    public void start() {

        account_id.setListener(this);
        //se.timer.createEvent(this, 0);
        //   consoleDialog = new ManTraderConsoleDialog(Globals.frame, false, account_id);

//        this.consoleDialog.getBalancePanel().updateBalance(this.getAccount());
        // consoleDialog.     rdersList1.account=trader.getAccount();
//        consoleDialog.getConsole().trader=this;
        //   consoleDialog.setVisible(true);
    }

    @Override
    public long processEvent(long t, Event e) {

//        OpenOrdersList ol = this.consoleDialog.getConsole().getOrderListPanel();
//        ol.updateModel();
        return 1000;
    }

    @Override
    public String getDisplayName() {
        return "Human Trader";
    }

    @Override
    public AutoTraderGui getGui() {
        return new ManTraderGui(this);

    }

    @Override
    public JSONObject getConfig() {

        JSONObject cfg = new JSONObject();
        cfg.put("sound_file", soundFile);
        return cfg;
    }

    @Override
    public void setConfig(JSONObject cfg) {
        soundFile = cfg.optString("sound_file", null);

    }

    @Override
    public boolean getDevelStatus() {
        return true;
    }

    @Override
    public JDialog getGuiConsole(Frame parent) {
        if (consoleDialog!=null){
            return consoleDialog;
        }
        
        consoleDialog = new ManTraderConsoleDialog(parent, false, se, account_id, this);

        consoleDialog.init(se, account_id);
        consoleDialog.doUpdate(account_id, this);
        consoleDialog.setLocationRelativeTo(parent);
        consoleDialog.pack(); //
        consoleDialog.setMinimumSize(consoleDialog.getSize());        
        consoleDialog.setTitle(account_id.getOwner().getName() + " - Trading Console");
        return this.consoleDialog;
    }
    
    @Override
    public void stop(){
        if (consoleDialog!=null)
            consoleDialog.dispose();
    }

    volatile Clip clip;

    /**
     * Startet einen neuen Thread, der die angegebene WAV-Datei abspielt und
     * wartet, bis die Wiedergabe abgeschlossen ist, bevor der Thread endet. Die
     * Funktion kehrt SOFORT zurück.
     *
     * @param filePath Der Pfad zur WAV-Datei.
     */
    public void startSoundAsynchronously(String filePath) {
        /*        if (clip != null) {
            clip.stop();
        }
        clip = null;*/

        // Erstellen Sie einen neuen Thread (oder Runnable) für die Sound-Wiedergabe
        Thread soundThread = new Thread(() -> {
            try {
                if (clip != null) {
                    clip.stop();
                    while (clip.isRunning()) {
                        Thread.sleep(5);
                    }
                }

                Thread.sleep(50);

                clip = null;

                File soundFile = new File(filePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();

                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
               //     System.out.printf("Gain Control in Action\n");
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // Stellt die Lautstärke auf einen leiseren Wert (z.B. -15 dB)
                    gainControl.setValue(-85.0f);
                }

                // Ein Lock-Objekt ist hier nicht mehr nötig, da der Thread auf sich selbst wartet
                // Fügt den LineListener hinzu
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        // Stoppt die Clip-Wiedergabe und schließt den Clip
                        clip.close();
                    }
                });

                clip.open(audioIn);
                clip.start();

                // Da dieser Thread *nur* für den Sound existiert, lassen wir ihn laufen, 
                // bis der Clip beendet wird.
                // Wir verwenden eine Schleife, die läuft, solange der Clip aktiv ist.
                while (clip.isRunning()) {
//                    System.out.printf("Clip is running\n");
                    Thread.sleep(100);
                }

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Fehler beim Laden/Öffnen des Sounds: " + e.getMessage());
            } catch (InterruptedException e) {
                // Thread wurde extern unterbrochen
                Thread.currentThread().interrupt();
                System.err.println("Sound-Thread unterbrochen.");
            }
        });
        soundThread.start();
    }

    @Override
    public void accountUpdated(Account a, Order o) {

        this.allOrders.put(o.getID(), o);
   //     System.out.printf("Update received %d\n", this.allOrders.size());

        if (o.getStatus() == Order.CLOSED) {
            if (soundFile != null && soundFile.length() > 0) {
                ManTrader.startSoundFX(soundFile);
            }
        }

        if (this.consoleDialog == null) {
            return;
        }

        //this.consoleDialog.cons
        //System.out.printf("AccountListener called\n");
        //System.out.printf("%d %s\n", o.getID(), o.getStatus().toString());
        if (o.getStatus() == Order.CLOSED) {
//            o.getAccount().getOrders().put(o.getID(), o);
        }

        consoleDialog.doUpdate(a, this);

        //  this.consoleDialog.getOrderList().updateModel();
        //  this.consoleDialog.getBalancePanel().updateBalance(o.getAccount());
    }

    private static MediaPlayer currentMediaPlayer;

    public static void startSoundFX(String filePath) {
        // Stoppe den vorherigen Player
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.dispose(); // Ressourcen freigeben
            currentMediaPlayer = null;
        }

        try {
            // Da JavaFX asynchron läuft, muss der gesamte Aufruf in einem Platform.runLater erfolgen
            Platform.runLater(() -> {
                try {
                    File file = new File(filePath);
                    Media sound = new Media(file.toURI().toString());
                    MediaPlayer player = new MediaPlayer(sound);

                    // 1. Lautstärke setzen (optional, aber empfohlen)
                    // Hier steuern Sie die interne JavaFX-Lautstärke (0.0 bis 1.0)
                    player.setVolume(0.5);

                    // 2. Registrieren des aktuellen Players für die Abbruchlogik
                    currentMediaPlayer = player;

                    // 3. Wiedergabe starten und automatisch nach dem Ende bereinigen
                    player.setOnEndOfMedia(() -> {
                        player.dispose(); // Wichtig: Ressourcen freigeben
                        if (currentMediaPlayer == player) {
                            currentMediaPlayer = null;
                        }
                    });

                    player.play();
                } catch (MediaException me) {
                    // Fangen Sie spezifisch die JavaFX MediaException ab
                    System.out.println("Fehler im JavaFX-Thread beim Erstellen des Players: " + me.getMessage());
                } catch (Exception e) {
                    // Fangen Sie andere Laufzeitfehler innerhalb des Lambda ab
                    System.out.println("Unerwarteter Fehler im JavaFX-Thread: " + e.getMessage());
                }

            });

        } catch (Exception e) {
            System.out.println("Fehler beim Abspielen des JavaFX Sounds: " + e.getMessage());
            //e.printStackTrace();
        }
    }

}
