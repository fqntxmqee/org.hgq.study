
package org.hgq.study.redis.model;

public class Range {

	private static final int	SIZE	= 9;

	public int						being	= 0;

	public int						end	= SIZE;

	public Range() {
	};

	public Range(int begin, int end) {
		this.being = begin;
		this.end = end;
	}

	public Range(int pageNumber) {
		this.being = 0;
		this.end = pageNumber * SIZE;
	}

	public int getPages() {
		return (int) Math.round(Math.ceil(end / SIZE));
	}
}
