package com.leakedbits.codelabs.box2d;

public class Tuple<X, Y> {

	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<X, Y> other = (Tuple<X, Y>) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}


}
