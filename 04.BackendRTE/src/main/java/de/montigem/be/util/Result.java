/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<OkType, ErrType> {
  public static <OkType, ErrType> Result<OkType, ErrType> ok(OkType value) {
    return new Result<>(Optional.of(value), Optional.empty());
  }

  public static <OkType, ErrType> Result<OkType, ErrType> ok(Optional<OkType> value) {
    return new Result<>(value, Optional.empty());
  }

  public static <OkType, ErrType> Result<OkType, ErrType> err(ErrType value) {
    return new Result<>(Optional.empty(), Optional.of(value));
  }

  public static <OkType, ErrType> Result<OkType, ErrType> err(Optional<ErrType> value) {
    return new Result<>(Optional.empty(), value);
  }

  private final Optional<OkType> ok;
  private final Optional<ErrType> err;

  private Result(Optional<OkType> ok, Optional<ErrType> err) {
    if (ok.isPresent() && err.isPresent()) {
      throw new RuntimeException("0xFE30: can't set ok and err");
    }
    if (!ok.isPresent() && !err.isPresent()) {
      throw new RuntimeException("0xFE31: either ok or err has to be set");
    }

    this.ok = ok;
    this.err = err;
  }

  public <T> T map(
      Function<? super OkType, ? extends T> okFunc,
      Function<? super ErrType, ? extends T> errFunc) {
    return ok.<T>map(okFunc).orElseGet(() -> err.map(errFunc).get());
  }

  public <T> Result<T, ErrType> onOk(Function<? super OkType, ? extends T> lFunc) {
    return new Result<>(ok.map(lFunc), err);
  }

  public <T> Result<OkType, T> onErr(Function<? super ErrType, ? extends T> rFunc) {
    return new Result<>(ok, err.map(rFunc));
  }

  public void apply(Consumer<? super OkType> lFunc, Consumer<? super ErrType> rFunc) {
    ok.ifPresent(lFunc);
    err.ifPresent(rFunc);
  }

  public boolean isOk() {
    return ok.isPresent();
  }

  public boolean isErr() {
    return err.isPresent();
  }

  public Optional<OkType> getOkOptional() {
    return ok;
  }

  public Optional<ErrType> getErrOptional() {
    return err;
  }

  public OkType getOk() {
    return ok.get();
  }

  public ErrType getErr() {
    return err.get();
  }

  public OkType get() throws RuntimeException {
    if (!ok.isPresent()) {
      throw new RuntimeException("0xFE3A: Couldn't get object, was error: " + err.get());
    }

    return ok.get();
  }
}
