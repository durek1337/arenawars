package content;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MapFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
int w,h;
Room r;
JPanel p;



		public MapFrame(Room r){
			super(r.getID());
			r.mapframe = this;

			this.w = r.map.w;
			this.h = r.map.h;
			this.r = r;
	        setResizable(true);
	        
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        
			this.p = new JPanel(){
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g){
					super.paintComponent(g);
					if(r.running){
					synchronized(r.chars){
					for(Character c : r.chars){
						g.setColor(Color.BLACK);
						g.fillPolygon(c.rec.pol);
						g.setColor(Color.RED);
						synchronized(c.shots){
							for(Projectile p : c.shots.values())
								g.drawPolygon(p.rec.pol);
						}
					}
					}
					synchronized(r.map.weapons){
						for(Rectangle rec : r.map.weapons.keySet()){
							g.fillPolygon(rec.pol);
						}
					}
					g.setColor(Color.GREEN);
					synchronized(r.map.powerups){
						for(Rectangle rec : r.map.powerups.keySet()){
							g.fillPolygon(rec.pol);
						}
					}
					g.setColor(Color.BLUE);
					synchronized(r.map.gameobjects){
						for(Rectangle rec : r.map.gameobjects.keySet()){
							g.fillPolygon(rec.pol);
						}
					}
					
					}
					
				}
				};
				Dimension dim = new Dimension(this.w,this.h);
				p.setPreferredSize(dim);
				JMenuBar menubar = new JMenuBar();
					JMenu basicMenu = new JMenu("Einstellungen");
					JMenu basicMenuSpeed = new JMenu("Geschwindigkeit");
						JMenu basicMenuSpeedProj = new JMenu("Projektile");
						basicMenuSpeedProj.add("Faktor: ");
						JTextField basicMenuSpeedProjFac = new JTextField(""+this.r.movementspeed);
						basicMenuSpeedProjFac.addActionListener((event) -> {
							this.r.setProjectilespeed(Double.parseDouble(basicMenuSpeedProjFac.getText()));
							basicMenuSpeedProjFac.setText(""+this.r.projectilespeed);
						});
						basicMenuSpeedProj.add(basicMenuSpeedProjFac);
						
						JMenu basicMenuSpeedChar = new JMenu("Charaktere");
						basicMenuSpeedChar.add("Faktor: ");
						JTextField basicMenuSpeedCharFac = new JTextField(""+this.r.movementspeed);
						basicMenuSpeedCharFac.addActionListener((event) -> {
							this.r.setMovementspeed(Double.parseDouble(basicMenuSpeedCharFac.getText()));
							basicMenuSpeedCharFac.setText(""+this.r.movementspeed);
						});
						basicMenuSpeedChar.add(basicMenuSpeedCharFac);
					
					basicMenuSpeed.add(basicMenuSpeedProj);
					basicMenuSpeed.add(basicMenuSpeedChar);
					
					basicMenu.add(basicMenuSpeed);
					JMenu addMenu = new JMenu("Hinzufügen");
						JMenuItem addMenuPowerup = new JMenuItem("PowerUp");
						addMenuPowerup.addActionListener((event) -> {
							this.r.addRandomPowerup();
						});
						addMenu.add(addMenuPowerup);
					
					menubar.add(basicMenu);
					menubar.add(addMenu);
//				this.setPreferredSize(dim);
				this.setJMenuBar(menubar);
				this.add(p);
				this.setVisible(true);
				this.pack();

				
				new Thread(this).start();
			
		}
		@Override
		public void run() {
			while(this.isVisible()){
				this.p.repaint();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}



}
