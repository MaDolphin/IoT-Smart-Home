package de.montigem.be.util.tuple;

public final class FourTuple<A, B, C, D>{
  public final A first;
  public final B second;
  public final C third;
  public final D fourth;

  public static <A, B, C, D> FourTuple<A, B, C, D> of(A first, B second, C third, D fourth) {
    return new FourTuple(first, second, third, fourth);
  }

  public FourTuple(A first, B second, C third, D fourth){
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
  }

  public A getFirst() {
    return this.first;
  }

  public B getSecond() {
    return this.second;
  }

  public C getThird() {
    return this.third;
  }

  public D getFourth() {
    return this.fourth;
  }
}