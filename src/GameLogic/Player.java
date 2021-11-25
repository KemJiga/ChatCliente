/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameLogic;

/**
 *
 * @author kemer
 */
public class Player {
    private int playerHp;

    public int getPlayerHp() {
        return playerHp;
    }
    
    public Player(){
        this.playerHp = 100;
    }
    
    public void takeDmg(int dmg){
        this.playerHp -= dmg;
    }
    
    public void heal(int heal){
        this.playerHp += heal;
    }
}
