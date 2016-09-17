package io.dynam.game.domain;

import java.awt.geom.Point2D;

import javax.persistence.*;

@Embeddable
public class Location {
	public static final Location DEFAULT = new Location(new Point2D.Float(0, 0),
			new Point2D.Float(0, 0), new Point2D.Float(0, 0));

	@Embedded
	private Point2D _position;
	@Embedded
	private Point2D _velocity;
	@Embedded
	private Point2D _acceleration;

	protected Location() {
	}

	public Location(Point2D position, Point2D velocity, Point2D acceleration) {
		_position = position;
		_velocity = velocity;
		_acceleration = acceleration;
	}
}
