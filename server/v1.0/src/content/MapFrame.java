package content;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
					synchronized(r.map.weaopons){
						for(Rectangle rec : r.map.weaopons.keySet()){
							g.drawPolygon(rec.pol);
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
				p.setPreferredSize(new Dimension(this.w, this.h));
				add(p);
				pack();
				setVisible(true);
				
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
