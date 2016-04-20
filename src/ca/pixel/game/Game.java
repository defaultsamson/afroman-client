package ca.pixel.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.entity.PlayerEntity;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.input.InputHandler;
import ca.pixel.game.world.Level;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	
	private JFrame frame;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Texture screen = new Texture(((DataBufferInt) image.getRaster().getDataBuffer()).getData(), WIDTH, HEIGHT);
	
	private boolean fullscreen = false;
	
	public boolean running = false;
	public int tickCount = 0;
	
	public InputHandler input = new InputHandler(this);
	public Level blankLevel = new Level(64, 64);
	public PlayerEntity player = new PlayerEntity(blankLevel, 100, 200, 1, input);
	
	public Game()
	{
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		player.setCameraToFollow(true);
	}
	
	@Override
	public void validate()
	{
		super.validate();
		
		resizeGame(this.getWidth(), this.getHeight());
	}
	
	/**
	 * Operation to perform to resize the game, keeping the aspect ratio.
	 * 
	 * @param windowWidth the new desired width.
	 * @param windowHeight the new desired height.
	 */
	public void resizeGame(int windowWidth, int windowHeight)
	{
		int newWidth = 0;
		int newHeight = 0;
		
		// If what the drawn height should be based on the width goes off screen
		if (windowWidth / 16 * 9 > windowHeight)
		{
			newWidth = windowHeight / 9 * 16;
			newHeight = windowHeight;
		}
		else // Else do the height based on the width
		{
			newWidth = windowWidth;
			newHeight = windowWidth / 16 * 9;
		}
		
		// Resizes the canvas to match the new window size, keeping it centred.
		setBounds((windowWidth - newWidth) / 2, (windowHeight - newHeight) / 2, newWidth, newHeight);
	}
	
	public synchronized void start()
	{
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop()
	{
		running = false;
	}
	
	@Override
	public void run()
	{
		long lastTime = System.nanoTime();
		double ticksPerSecond = 60D;
		double nsPerTick = 1000000000D / ticksPerSecond;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while (running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true; // true for unlimited frames, false for limited to tick rate
			
			while (delta >= 1)
			{
				ticks++;
				tick();
				delta--;
				shouldRender = true;
			}
			
			// Stops system from overloading
			try
			{
				Thread.sleep(2);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// Only render when something has been updated
			if (shouldRender)
			{
				frames++;
				render();
			}
			
			// If current time - the last time we updated is >= 1 second
			if (System.currentTimeMillis() - lastTimer >= 1000)
			{
				System.out.println("(TPS: " + ticks + ", FPS: " + frames + ")");
				lastTimer += 1000;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick()
	{
		tickCount++;
		
		if (input.full_screen.isPressedFiltered())
		{
			System.out.println("Pressed");
			
			// Toggles Fullscreen Mode
			setFullScreen(!fullscreen);
		}
		
		blankLevel.tick();
	}
	
	public void render()
	{
		// Clears the canvas
		image.getGraphics().setColor(Color.WHITE);
		image.getGraphics().fillRect(0, 0, getWidth(), getHeight());
		
		blankLevel.render(screen);
		
		// screen.draw(Assets.player, (WIDTH / 2) - 8, 60);
		
		if (this.tickCount < 240)
		{
			int xPos = (WIDTH / 2);
			int yPos = 120;
			
			Assets.font_normal.renderCentered(screen, WIDTH - xPos, HEIGHT - yPos, "CANCER:");
			Assets.font_normal.renderCentered(screen, WIDTH - xPos, HEIGHT + 15 - yPos, "The Adventures of");
			Assets.font_normal.renderCentered(screen, WIDTH - xPos, HEIGHT + 25 - yPos, "Afro Man");
		}
		
		// Renders everything that was just drawn
		BufferStrategy bs = getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		Graphics2D g = ((Graphics2D) bs.getDrawGraphics());
		// g.rotate(Math.toRadians(1), WIDTH /2, HEIGHT/2);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public void setFullScreen(boolean isFullScreen)
	{
		fullscreen = isFullScreen;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		System.out.println("Setting Fullscreen: " + isFullScreen);
		
		/*
		 * This StackOverFlow thread was EXTREMELY helpful in getting this to work properly
		 * http://stackoverflow.com/questions/13064607/fullscreen-swing-components-fail-to-receive-keyboard-input-on-java-7-on-mac-os-x
		 */
		if (isFullScreen)
		{
			frame.dispose();// Restarts the JFrame
			frame.setResizable(false);// Disables resizing else causes bugs
			frame.setUndecorated(true);
			frame.setVisible(true);
			frame.revalidate();
			this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			try
			{
				gd.setFullScreenWindow(frame);// Makes it full screen
				
				// if (System.getProperty("os.name").indexOf("Mac OS X") >= 0)
				// {
				// this.setVisible(false);
				// this.setVisible(true);
				// }
				
				this.repaint();
				this.revalidate();
			}
			catch (Exception e)
			{
				setFullScreen(false);
				System.err.println("Fullscreen Mode not supported.");
				e.printStackTrace();
			}
		}
		else
		{
			frame.dispose();// Restarts the JFrame
			frame.setVisible(false);
			frame.setResizable(true);
			frame.setUndecorated(false);
			frame.setVisible(true);// Shows restarted JFrame
			frame.pack();
			frame.setExtendedState(frame.getExtendedState() | JFrame.NORMAL);// Returns to maximized state
		}
		
		this.requestFocus();
	}
	
	public static void main(String[] args)
	{
		new Game().start();
	}
}
