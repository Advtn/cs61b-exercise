package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    GuitarString[] gs = new GuitarString[keyboard.length()];

    public GuitarHero(){
        getFrequency();
    }
    private void getFrequency(){
        for(int i = 0; i < gs.length; i++){
            double fre = 440 * Math.pow(2, (double) (i - 24) / 12);
            gs[i] = new GuitarString(fre);
        }
    }

    public static void main(String[] args) {
        GuitarHero L = new GuitarHero();

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int indexKey = L.keyboard.indexOf(key);
                if(indexKey != -1){
                    L.gs[indexKey].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for(GuitarString g : L.gs){
                sample += g.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString g : L.gs) {
                g.tic();
            }
        }
    }
}
