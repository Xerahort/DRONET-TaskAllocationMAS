package com.dronet.frontend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Task;
import com.dronet.mas.utils.shapes.CircleArea;

public class SceneDrawer extends JPanel {
	
	private static final long serialVersionUID = 6951449519049934693L;
	
	private static final String IMAGE_BACKGROUND = "background-chiyoda";
	public static final String IMAGE_DRONE_DELIVER_WITH_CAMERA = "dronedelcam";
	public static final String IMAGE_DRONE_DELIVER_NO_CAMERA = "dronecam";
	public static final String IMAGE_DRONE_SEARCH = "dronesearch";
	
	public static final String IMAGE_WAYPOINT_DELIVER = "waypointdeliver";
	public static final String IMAGE_WAYPOINT_SEARCH = "waypointsearch";
	public static final String IMAGE_WAYPOINT_VIDEO = "waypointvideo";
	
	static volatile private Graphics2D brush;
	static volatile private Map<String,Point> waypoints;
	static volatile private Map<String,Point> drones;
	static volatile private Map<String,CircleArea> droneAreas;
	static volatile private Map<String,String> allocations;
	static volatile private Map<String,BufferedImage> images;
	
	public SceneDrawer() {
		super();
		drones = Collections.synchronizedMap(new HashMap<String,Point>());
		droneAreas = Collections.synchronizedMap(new HashMap<String,CircleArea>());
		waypoints = Collections.synchronizedMap(new HashMap<String,Point>());
		images = Collections.synchronizedMap(new HashMap<String,BufferedImage>());
		allocations = Collections.synchronizedMap(new HashMap<String,String>());
		
		try {
			images.put(IMAGE_BACKGROUND, ImageIO.read(new File("assets/scenario-background.png")));
			images.put(IMAGE_DRONE_DELIVER_WITH_CAMERA, ImageIO.read(new File("assets/drone-deliver-with-camera.png")));
			images.put(IMAGE_DRONE_DELIVER_NO_CAMERA, ImageIO.read(new File("assets/drone-deliver-no-camera.png")));
			images.put(IMAGE_DRONE_SEARCH, ImageIO.read(new File("assets/drone-search.png")));
			images.put(IMAGE_WAYPOINT_DELIVER, ImageIO.read(new File("assets/waypoint-deliver.png")));
			images.put(IMAGE_WAYPOINT_SEARCH, ImageIO.read(new File("assets/waypoint-search.png")));
			images.put(IMAGE_WAYPOINT_VIDEO, ImageIO.read(new File("assets/waypoint-video.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void paint(Graphics g) {
		super.paintComponent(g);

		brush = (Graphics2D) g;
		
		paintBackground();
		paintShapes();
		paintAllocations();
		paintWaypoints();
		paintDrones();
		
/*		// draw GeneralPath (polyline)
		int x2Points[] = {10, 100, 0, 100};
		int y2Points[] = {0, 50, 50, 0};
		GeneralPath polyline = 
		        new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

		polyline.moveTo(x2Points[0], y2Points[0]);

		for (int index = 1; index < x2Points.length; index++) {
		         polyline.lineTo(x2Points[index], y2Points[index]);
		};

		brush.draw(polyline);
		
		g2d.translate(300, 300);
		g2d.setColor( Color.RED );
		g2d.drawLine( 5, 30, 380, 30 );
		g2d.setColor( Color.BLUE );
		g2d.drawRect( 5, 40, 90, 55 );
		g2d.fillRect( 100, 40, 90, 55 );
		g2d.setColor( Color.BLACK );
		g2d.fillRoundRect( 195, 40, 90, 55, 50, 50 );
		g2d.drawRoundRect( 290, 40, 90, 55, 20, 20 );
		g2d.setColor( Color.YELLOW );
		g2d.draw3DRect( 5, 100, 90, 55, true );
		g2d.fill3DRect( 100, 100, 90, 55, false );
		g2d.setColor( Color.MAGENTA );
		g2d.drawOval( 195, 100, 90, 55 );
		g2d.fillOval( 290, 100, 90, 55 );*/
	}

	private void paintBackground() {

		BufferedImage img = images.get(IMAGE_BACKGROUND);
		int x = (getWidth() - img.getWidth()) / 2;
		int y = (getHeight() - img.getHeight()) / 2;
		brush.drawImage(img, x, y, this);
		
	}

	private void paintDrones() {
		
		Set<String> keySet = drones.keySet();
		
		for (String key : keySet) {
			
			String[] split = key.split(":");
			Point p = drones.get(key);
			int typeIndex = split.length-1;
			
			String image = split[typeIndex];
			
			BufferedImage img = images.get(image);
			brush.drawImage(img.getScaledInstance(30, 30, Image.SCALE_AREA_AVERAGING), p.x-15, p.y-15, this);
		}
		
	}
	
	private void paintShapes() {
		
		Set<String> keySet = droneAreas.keySet();
		
		for (String key : keySet) {
			
			String[] split = key.split(":");
			CircleArea p = droneAreas.get(key);
			int typeIndex = split.length-1;
			
			switch (split[typeIndex]) {
			case IMAGE_DRONE_DELIVER_WITH_CAMERA:
				brush.setColor(Color.BLACK);
				break;
			case IMAGE_DRONE_DELIVER_NO_CAMERA:
				brush.setColor(Color.RED);
				break;
			case IMAGE_DRONE_SEARCH:
				brush.setColor(Color.BLUE);
				break;
			default:
				brush.setColor(Color.BLACK);
				break;
			}
			
			drawCenteredCircle((int)p.getCenter().getLongitude(), (int)p.getCenter().getLatitude(), (int)p.getRatio());
		}
		
		brush.setColor(Color.BLACK);
	}

	private void paintWaypoints() {
		try {
			
			Set<String> keySet = waypoints.keySet();
			
			for (String key : keySet) {
				
				Point p = waypoints.get(key);
				
				String[] split = key.split(":");
				int typeIndex = split.length-1;
				
				String image = split[typeIndex];
				
				BufferedImage img = images.get(image);
				brush.drawImage(img.getScaledInstance(13, 18, Image.SCALE_AREA_AVERAGING), (int)p.getX()-6, (int)p.getY()-18, this);
			} 
		} catch (java.util.ConcurrentModificationException e) {
			System.err.println("concurrent exception during painting");
		}
	}
	
	private void paintAllocations() {
		
		brush.setColor(Color.BLUE);
		
		for (String drone : allocations.keySet()) {
			String task = allocations.get(drone);
			
			GeneralPath polyline = 
			        new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);

			Point droneCoords = drones.get(drone);
			Point taskCoords = waypoints.get(task);
			
			try {
				
				polyline.moveTo(droneCoords.getX(), droneCoords.getY());
				polyline.lineTo(taskCoords.getX(), taskCoords.getY());
				
			} catch (NullPointerException e) {
				System.out.println(drone + "|" + droneCoords + " - " + task + "|" + taskCoords);
				
			}
			
			brush.draw(polyline);
		}	
		
		brush.setColor(Color.BLACK);
	}
	
	public void drawCenteredCircle(int x, int y, int r) {
		  x = x-r;
		  y = y-r;
		  brush.drawOval(x,y,2*r,2*r);
	}

	public synchronized void setWaypoint(int x, int y, String name, Task.Action action) {
		
		switch (action) {
		case SEARCH:
			waypoints.put(name + ":" + IMAGE_WAYPOINT_SEARCH, new Point(x, y));
			break;
		case SENSOR:
			waypoints.put(name + ":" + IMAGE_WAYPOINT_VIDEO, new Point(x, y));
			break;
		default:
			waypoints.put(name + ":" + IMAGE_WAYPOINT_DELIVER, new Point(x, y));
		}
		
		//waypoints.put(name, new Point(x, y));
		//repaint();
	}
	
	public synchronized void setDrone(int x, int y, CircleArea shape, String name, Drone.Role role) {
		
		switch (role) {
		case SEARCH:
			drones.put(name + ":" + IMAGE_DRONE_SEARCH, new Point(x, y));
			droneAreas.put(name + ":" + IMAGE_DRONE_SEARCH, shape);
			break;
		case DELIVER_NO_CAMERA:
			drones.put(name + ":" + IMAGE_DRONE_DELIVER_NO_CAMERA, new Point(x, y));
			droneAreas.put(name + ":" + IMAGE_DRONE_DELIVER_NO_CAMERA, shape);
			break;
		default:
			drones.put(name + ":" + IMAGE_DRONE_DELIVER_WITH_CAMERA, new Point(x, y));
			droneAreas.put(name + ":" + IMAGE_DRONE_DELIVER_WITH_CAMERA, shape);
		}
		
		//drones.put(name, new Point(x, y));
		//droneAreas.put(name, shape);
		//repaint();
	}
	
	public synchronized void setAllocation(String drone, Drone.Role role, String task, Task.Action action) {
		
		String taskKey;
		switch (action) {
		case SEARCH:
			taskKey = task + ":" + IMAGE_WAYPOINT_SEARCH;
			break;
		case SENSOR:
			taskKey = task + ":" + IMAGE_WAYPOINT_VIDEO;
			break;
		default:
			taskKey = task + ":" + IMAGE_WAYPOINT_DELIVER;
		}
		
		String droneKey;
		switch (role) {
		case SEARCH:
			droneKey = drone + ":" + IMAGE_DRONE_SEARCH;
			break;
		case DELIVER_NO_CAMERA:
			droneKey = drone + ":" + IMAGE_DRONE_DELIVER_NO_CAMERA;
			break;
		default:
			droneKey = drone + ":" + IMAGE_DRONE_DELIVER_WITH_CAMERA;

		}
		
		allocations.put(droneKey,taskKey);
		//repaint();
	}
	
	public synchronized Point removeWaipoint(String name, Task.Action action) {
		
		String taskKey;
		switch (action) {
		case SEARCH:
			taskKey = name + ":" + IMAGE_WAYPOINT_SEARCH;
			break;
		case SENSOR:
			taskKey = name + ":" + IMAGE_WAYPOINT_VIDEO;
			break;
		default:
			taskKey = name + ":" + IMAGE_WAYPOINT_DELIVER;
		}
		
		return waypoints.remove(taskKey);
	}
	
	public synchronized Point removeDrone(String name, Drone.Role role) {
		
		String droneKey;
		switch (role) {
		case SEARCH:
			droneKey = name + ":" + IMAGE_DRONE_SEARCH;
			break;
		case DELIVER_NO_CAMERA:
			droneKey = name + ":" + IMAGE_DRONE_DELIVER_NO_CAMERA;
			break;
		default:
			droneKey = name + ":" + IMAGE_DRONE_DELIVER_WITH_CAMERA;

		}
		
		return drones.remove(droneKey);
	}
	
	public synchronized String removeAllocation(String drone, Drone.Role role) {
		
		String droneKey;
		switch (role) {
		case SEARCH:
			droneKey = drone + ":" + IMAGE_DRONE_SEARCH;
			break;
		case DELIVER_NO_CAMERA:
			droneKey = drone + ":" + IMAGE_DRONE_DELIVER_NO_CAMERA;
			break;
		default:
			droneKey = drone + ":" + IMAGE_DRONE_DELIVER_WITH_CAMERA;

		}
		
		return allocations.remove(droneKey);
	}
}