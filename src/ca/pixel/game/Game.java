package ca.pixel.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.gfx.Texture;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 5;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	
	private JFrame frame;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Texture pixels = new Texture(((DataBufferInt) image.getRaster().getDataBuffer()).getData(), WIDTH, HEIGHT);
	
	public boolean running = false;
	public int tickCount = 0;
	
	private Texture player = Assets.sheet1.getSubTexture(0, 0, 8, 16);
	public InputHandler input = new InputHandler(this);
	
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
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
		
		if (input.up.isPressed())
		{
			yPos--;
		}
		if (input.down.isPressed())
		{
			yPos++;
		}
		if (input.left.isPressed())
		{
			xPos--;
		}
		if (input.right.isPressed())
		{
			xPos++;
		}
	}
	
	private int xPos = (WIDTH / 2) - 4;
	private int yPos = 60;
	
	public void render()
	{
		// Clears the canvas
		image.getGraphics().setColor(Color.WHITE);
		image.getGraphics().fillRect(0, 0, getWidth(), getHeight());
		
		Texture drawPlayer = player.clone();
		// drawPlayer.rotate180();
		
		Assets.font_normal.renderCentered(pixels, WIDTH / 2, 20, "CANCER:");
		Assets.font_normal.renderCentered(pixels, WIDTH / 2, 35, "The Adventures of");
		Assets.font_normal.renderCentered(pixels, WIDTH / 2, 45, "Afro Man");
		
		pixels.draw(drawPlayer, xPos, yPos);
		
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
	
	public static void main(String[] args)
	{
		new Game().start();
	}
}
