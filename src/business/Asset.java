
package business;

import static java.awt.SystemColor.text;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JFileChooser;

/**
 *
 * @author nick Riley
 */
public class Asset {
    private String nm, emsg;
    private double cost, salvage;
    private int life;
    private boolean built;
    private double[][] begbal, anndep, endbal;
    private static final int SL = 0;
    private static final int DDL = 1;
    
    public Asset(){
      //Empty constructor
        this.nm = "";
        this.cost = 0;
        this.salvage = 0;
        this.life = 0;
        this.built = false;
    }
    
    public Asset(String nm, double cost, double salvage, int life) {
        this.nm = nm;
        this.cost = cost;
        this.salvage = salvage;
        this.life = life;
        if(isValid()){
            buildDep();
        }
    } // End of Construstor
    
    private boolean isValid(){
        this.emsg = "";
        if(this.nm.trim().isEmpty()){
            this.emsg += "Asset name is missing. ";
        }
        if(this.cost <= 0){
            this.emsg += "Cost must be greater than zero. ";
        }
        if(this.salvage <= 0){
            this.emsg += "Salvage must be greater than zero. ";
        }
        if(this.life <= 0){
            this.emsg += "Life must be greater than zero. ";
        }
        if(this.salvage >= this.cost){
            this.emsg += "Salvage must be less than cost. ";
        }
        return this.emsg.isEmpty();
    }
    
    private void buildDep(){
        try {
            // Formula for Depreciation amount
            double anndepSL = (this.cost - this.salvage) / this.life;
            double rateDDL = (1.0 / this.life) * 2.0;
            double wrkDDL;

            this.begbal = new double[this.life][2];
            this.anndep = new double[this.life][2];
            this.endbal = new double[this.life][2];

            this.begbal[0][SL] = this.cost;
            this.begbal[0][DDL] = this.cost;

            for(int i = 0; i < this.life; i++){
                if(i > 0){
                    this.begbal[i][SL] = this.endbal[i-1][SL];
                    this.begbal[i][DDL] = this.endbal[i-1][DDL];
                } 
                // DDL annual depreciation calculated
                this.anndep[i][DDL] = begbal[i][DDL] * rateDDL;
                this.endbal[i][DDL] = this.begbal[i][DDL] - this.anndep[i][DDL];
                
                // Change to Straight line
                if(this.begbal[i][DDL] * rateDDL < anndepSL){ 
                    this.anndep[i][DDL] = anndepSL;
                    this.endbal[i][DDL] = this.begbal[i][DDL] - anndepSL;
                    // Adjust final deprciation
                    if(this.begbal[i][DDL] - anndepSL < this.salvage){   
                        this.anndep[i][DDL] = begbal[i][DDL] - this.salvage;
                        this.endbal[i][DDL] = 
                                this.begbal[i][DDL] - this.anndep[i][DDL];
                    }
                }
                
                this.anndep[i][SL] = anndepSL;
                this.endbal[i][SL] = this.begbal[i][SL] - this.anndep[i][SL];
                
            }   //End of For loop
            this.built = true;
        } catch(Exception e){
            this.emsg = "Build Error" + e.getMessage();
            this.built = false;
        }
    }
    public String getErrorMsg(){
        return this.emsg;
    }
    public int getLife(){
        return this.life;
    }
    public double getAnnDep(){
        if(!this.built){
            buildDep();
            if(!this.built){
                return -1;
            }
        }
        return this.anndep[0][SL];
    }
    public double getAnnDep(int yr){
        if(!this.built){
            buildDep();
            if(!this.built){
                return -1;
            }
        }
        if(yr < 1 || yr > this.life){
            return -1;
        }
        return this.anndep[yr-1][DDL];
    }
    public double getBegBal(int yr, String m){
        if(!this.built){
            buildDep();
            if(!this.built){
                return -1;
            }
        }
        if(yr < 1 || yr > this.life){
            return -1;
        }
        if(m.equalsIgnoreCase("s")){
            return this.begbal[yr-1][SL];
        } else if(m.equalsIgnoreCase("d")) {
            return this.begbal[yr-1][DDL];
        } else {
            return -1;
        }
    }
    public double getEndBal(int yr, String m){
        if(!this.built){
            buildDep();
            if(!this.built){
                return -1;
            }
        }
        if(yr < 1 || yr > this.life){
            return -1;
        }
        if(m.equalsIgnoreCase("s")){
            return this.endbal[yr-1][SL];
        } else if(m.equalsIgnoreCase("d")) {
            return this.endbal[yr-1][DDL];
        } else {
            return -1;
        }
    }
    
     public static void setSave(JFileChooser savefile, String save){
                          
            try{
            PrintWriter out =
                    new PrintWriter(new BufferedWriter(new FileWriter(savefile.getSelectedFile())));
                    out.write(save);
                    out.close();
                 } catch(Exception e){
                 }
         }
    
}// End of class
