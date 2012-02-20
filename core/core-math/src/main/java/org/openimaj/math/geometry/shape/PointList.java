package org.openimaj.math.geometry.shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;

import Jama.Matrix;

/**
 * A base implementation of a {@link GeometricObject} that is
 * a set of points in space.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 */
public class PointList implements GeometricObject, Iterable<Point2d>{
	/** The points in the {@link PointList} */
	public List<Point2d> points = new ArrayList<Point2d>();

	/**
	 * Construct a {@link PointList} from points
	 * @param points the points
	 */
	public PointList(Point2d... points) {
		for (Point2d v : points)
			this.points.add(v);
	}

	/**
	 * Construct a {@link PointList} from points
	 * @param points the points
	 */
	public PointList(Collection<? extends Point2d> points) {
		this( points, false );
	}

	/**
	 * Construct a {@link PointList} from the points, possibly 
	 * copying the points first
	 * @param points the points
	 * @param copy should the points be copied
	 */
	public PointList( Collection<? extends Point2d> points, boolean copy ) {
		if( !copy )
			this.points.addAll( points );
		else
		{
			for( Point2d p : points )
				this.points.add( new Point2dImpl( p.getX(), p.getY() ) );
		}
	}

	void rotate(Point2d point, Point2d origin, double angle) {
		double X = origin.getX() + ((point.getX() - origin.getX()) * Math.cos(angle) -
				(point.getY() - origin.getY()) * Math.sin(angle));

		double Y = origin.getY() + ((point.getX() - origin.getX()) * Math.sin(angle) +
				(point.getY() - origin.getY()) * Math.cos(angle));

		point.setX((float) X);
		point.setY((float) Y);
	}

	/**
	 * Rotate the {@link PointList} about the given origin with the given angle (in radians)
	 * @param origin the origin of the rotation
	 * @param angle the angle in radians
	 */
	public void rotate(Point2d origin, double angle) {
		for (Point2d p : points)
			rotate(p, origin, angle);
	}

	/**
	 * Rotate the {@link PointList} about (0,0) with the given angle (in radians)
	 * @param angle the angle in radians
	 */
	public void rotate(double angle) {
		this.rotate( new Point2dImpl(0,0), angle );
	}

	/**
	 * Compute the regular (oriented to the axes) bounding box
	 * of the {@link PointList}.
	 * 
	 * @return the regular bounding box
	 */
	@Override
	public Rectangle calculateRegularBoundingBox() {
		int xmin=Integer.MAX_VALUE, xmax=0, ymin=Integer.MAX_VALUE, ymax=0;

		for (Point2d p : points) {
			if (p.getX() < xmin) xmin = (int) Math.floor(p.getX());
			if (p.getX() > xmax) xmax = (int) Math.ceil(p.getX());
			if (p.getY() < ymin) ymin = (int) Math.floor(p.getY());
			if (p.getY() > ymax) ymax = (int) Math.ceil(p.getY());
		}

		return new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
	}

	/**
	 * Translate the {@link PointList}s position
	 *  
	 * @param x x-translation
	 * @param y y-translation
	 */
	@Override
	public void translate(float x, float y) {
		for (Point2d p : points) {
			p.setX(p.getX() + x);
			p.setY(p.getY() + y);
		}
	}

	/**
	 * Scale the {@link PointList} by the given amount about (0,0). Scalefactors
	 * between 0 and 1 shrink the {@link PointList}. 
	 * @param sc the scale factor.
	 */
	@Override
	public void scale(float sc) {
		for (Point2d p : points ) {
			p.setX(p.getX() * sc);
			p.setY(p.getY() * sc);
		}
	}

	/**
	 * 	Scale the {@link PointList} only in the x-direction by the given amount about
	 * 	(0,0). Scale factors between 0 and 1 will shrink the {@link PointList} 
	 *	@param sc The scale factor
	 *  @return this {@link PointList}
	 */
	public PointList scaleX( float sc )
	{
		for (Point2d p : points) {
			p.setX(p.getX() * sc);
		}
		return this;
	}

	/**
	 * 	Scale the {@link PointList} only in the y-direction by the given amount about
	 * 	(0,0). Scale factors between 0 and 1 will shrink the {@link PointList}
	 *	@param sc The scale factor
	 *  @return this {@link PointList}
	 */
	public PointList scaleY( float sc )
	{
		for (Point2d p : points) {
			p.setY(p.getY() * sc);
		}
		return this;
	}

	/**
	 * Scale the {@link PointList} by the given amount about (0,0). Scale factors
	 * between 0 and 1 shrink the {@link PointList}. 
	 * @param scx the scale factor in the x direction
	 * @param scy the scale factor in the y direction.
	 * @return this {@link PointList}
	 */
	public PointList scaleXY( float scx, float scy )
	{
		for (Point2d p : points) {
			p.setX(p.getX() * scx);
			p.setY(p.getY() * scy);
		}
		return this;
	}

	/**
	 * Scale the {@link PointList} by the given amount about the given point. 
	 * Scalefactors between 0 and 1 shrink the {@link PointList}.
	 * @param centre the centre of the scaling operation
	 * @param sc the scale factor
	 */
	@Override
	public void scale(Point2d centre, float sc) {
		this.translate( -centre.getX(), -centre.getY() );

		for (Point2d p : points ) {
			p.setX(p.getX() * sc);
			p.setY(p.getY() * sc);
		}

		this.translate( centre.getX(), centre.getY() );
	}

	/**
	 * Scale the {@link PointList} about its centre of gravity.
	 * Scalefactors between 0 and 1 shrink the {@link PointList}.
	 * @param sc the scale factor
	 */
	@Override
	public void scaleCOG( float sc )
	{
		Point2d cog = getCOG();
		this.translate( -cog.getX(), -cog.getY() );
		this.scale( sc );
		this.translate( cog.getX(), cog.getY() );
	}

	/**
	 * Get the centre of gravity of the {@link PointList}
	 * @return the centre of gravity of the {@link PointList}
	 */
	@Override
	public Point2d getCOG()
	{
		float xSum = 0;
		float ySum = 0;

		int n = 0;

		for (Point2d p : points ) {
			xSum += p.getX();
			ySum += p.getY();
			n++;
		}

		xSum /= n;
		ySum /= n;

		return new Point2dImpl( xSum, ySum );
	}

	/**
	 * @return the minimum x-ordinate of all vertices
	 */
	@Override
	public double minX() { return calculateRegularBoundingBox().x; }

	/**
	 * @return the minimum y-ordinate of all vertices
	 */
	@Override
	public double minY() { return calculateRegularBoundingBox().y; }

	/**
	 * @return the maximum x-ordinate of all vertices
	 */
	@Override
	public double maxX() { Rectangle r = calculateRegularBoundingBox(); return r.x+r.width; }

	/**
	 * @return the maximum y-ordinate of all vertices
	 */
	@Override
	public double maxY() { Rectangle r = calculateRegularBoundingBox(); return r.y+r.height; }

	/**
	 * @return the width of the regular bounding box 
	 */
	@Override
	public double getWidth() { return maxX()-minX(); }

	/**
	 * @return the height of the regular bounding box
	 */
	@Override
	public double getHeight() { return maxY()-minY(); }

	/**
	 * Apply a 3x3 transform matrix to a copy of the {@link PointList}
	 * and return it
	 * @param transform 3x3 transform matrix
	 * @return the transformed {@link PointList}
	 */
	@Override
	public PointList transform(Matrix transform) {
		List<Point2d> newVertices = new ArrayList<Point2d>();

		for (Point2d p : points) {
			Matrix p1 = new Matrix(3,1);
			p1.set(0,0, p.getX());
			p1.set(1,0, p.getY());
			p1.set(2,0, 1);

			Matrix p2_est = transform.times(p1);

			Point2d out = new Point2dImpl((float)(p2_est.get(0,0) / p2_est.get(2, 0)), (float)(p2_est.get(1,0) / p2_est.get(2, 0)));

			newVertices.add(out);
		}

		return new PointList(newVertices);
	}

	/**
	 *	{@inheritDoc}
	 * 	@see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Point2d> iterator()
	{
		return points.iterator();
	}

	@Override
	public String toString() {
		return points.toString();
	}

	/**
	 * Compute the mean of a set of {@link PointList}s. 
	 * It is assumed that the number of points in the {@link PointList}s
	 * is equal, and that their is a one-to-one correspondance between
	 * the ith point in each list.
	 * 
	 * @param shapes the shapes to average
	 * @return the average shape
	 */
	public static PointList computeMean(Collection<PointList> shapes) {
		final int npoints = shapes.iterator().next().points.size();
		PointList mean = new PointList();
		
		for (int i=0; i<npoints; i++) mean.points.add(new Point2dImpl());
		
		for (PointList shape : shapes) {
			for (int i=0; i<npoints; i++) {
				Point2dImpl pt = (Point2dImpl) mean.points.get(i);
				
				pt.x += shape.points.get(i).getX();
				pt.y += shape.points.get(i).getY();
			}
		}
		
		for (int i=0; i<npoints; i++) {
			Point2dImpl pt = (Point2dImpl) mean.points.get(i);
			
			pt.x /= shapes.size();
			pt.y /= shapes.size();
		}
		
		return mean;
	}
}