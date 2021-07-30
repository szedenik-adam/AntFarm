package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URLClassLoader;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import spray.SprayEnum;

public class Form extends JFrame {

	private static final long serialVersionUID = 2569202083084412295L;

	private RenderEngine engine = null;
	private JPanel contentPane;
	public boolean isClosing=false;
	private boolean exitMenuOpened = false;

	private static java.util.Timer gameTime = null;

	public static Form frame=null;

	public static int nextRound_ms=300;

	/** Launch the application.*/
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println("Frame main called");
					frame = new Form();
					frame.setVisible(true);
					System.out.println("Frame visible");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void playSound(String fn, boolean loop)
	{
		try {
			//java.net.URL defaultSound = getClass().getResource("/sounds/"+fn);
	        //File soundFile = new File(defaultSound.toURI());
	        //AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			java.net.URL defaultSound = URLClassLoader.getSystemResource("sounds/"+fn);
			if(defaultSound == null) {
				System.out.println("sound in null "+fn);
			}
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(defaultSound);
			Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
	        clip.start();
		}
		catch (Exception _ex)
		{
			System.out.println(_ex.getMessage());
		}
	}


	/**
	 * Create the frame.
	 */
	Dimension resolution = null;

	public Form() {
		System.out.println("Form::ctor");
		gameTime = new java.util.Timer();
		resolution = Toolkit.getDefaultToolkit().getScreenSize();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 800, 600);

		setBounds(0, 0,(int)resolution.getWidth(), (int)resolution.getHeight());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setUndecorated(true);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				//display.mouseclick();

				//Hova klikkeltünk a pályán
				Point destination = e.getPoint();

				destination.setLocation(destination.getX(), destination.getY());
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					//Hangyaolo spray
					if (engine.getSprayCharge(SprayEnum.AntKiller) > 0)
					{
						engine.sprayFired(SprayEnum.AntKiller,destination);
						playSound("spray1.wav",false);
					}
					else playSound("spray_empty.wav",false);
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					if (engine.getSprayCharge(SprayEnum.OdorNeutralizer) > 0)
					{
					//Szagsemlegesito spray
						engine.sprayFired(SprayEnum.OdorNeutralizer,destination);
						playSound("spray2.wav",false);
					}
					else playSound("spray_empty.wav",false);
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				engine.updateMousePos(e.getPoint());
			}
		});
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == 27) {
					if (!exitMenuOpened) {
						exitMenuOpened = true;
						JFrame exitMenu = new ExitMenu();
						exitMenu.setLocation(getWidth()/2-exitMenu.getWidth()/2, getHeight()/2-exitMenu.getHeight()/2);
						exitMenu.setVisible(true);
					}
				}

			}
		});


		engine=new RenderEngine(this);
		Thread renderThread=new Thread(engine);
		       renderThread.start();

		//Itt inditjuk a jatek Timer-t

		       gameTime.schedule(new TimerTask()
				{
					@Override
					public void run() {    // Szól az engine-nek, hogy
						engine.nextRound();// hívhatja a map nextRound-ját.
					}					   // (nem közvetlenül itt hívja meg a nextRound-ot)

				}, 0, Form.nextRound_ms);

		playSound("bg.wav",true);

		System.out.println("Form::ctor-finished");
	}

	@Override
	public void paint(Graphics g) {

		synchronized(engine){engine.notify();}



		BufferedImage img = engine.getCurrentImage();
		//synchronized(img) //kép lockolása
		{
         g.drawImage(img, 0, 0, Color.white, null); //!!!
         //try { img.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
         engine.form_num++;
         //try{Thread.sleep(1000);} catch (InterruptedException e) { e.printStackTrace(); }
		}

        //Újrarajzolás a formon
        this.repaint();
     }

	public void gameOver() {
		// TODO Auto-generated method stub

	}

	class ExitMenu extends JFrame {

		/**
		 *
		 */
		private static final long serialVersionUID = -1650358716207787139L;
		static private final int gombSzam = 3;

		public ExitMenu()
		{
			setBounds(0, 0, 200, gombSzam*30);
		    setUndecorated(true);
			setResizable(false);
			this.setAlwaysOnTop(true);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new GridLayout(0, 1));
			setContentPane(contentPane);

			JButton btn;

			btn = new JButton("Return to game");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					exitMenuOpened = false;
					ExitMenu.this.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			});
			contentPane.add(btn);

			btn = new JButton("New game");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					exitMenuOpened = false;
					ExitMenu.this.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					engine=new RenderEngine(Form.this);
					Thread renderThread=new Thread(engine);
					       renderThread.start();
				}
			});
			contentPane.add(btn);

			btn = new JButton("Exit");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Form.this.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					isClosing = true;
				}
			});
			contentPane.add(btn);
		}
	}

}
