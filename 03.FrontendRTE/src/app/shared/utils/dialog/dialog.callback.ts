export interface DialogCallback {
  onConfirm: () => void;
  onOption: () => void;
  onDeny: () => void;
}

export class DialogCallbackEmpty implements DialogCallback {
  public onConfirm(): void {
  }

  public onOption(): void {
  }

  public onDeny(): void {
  }
}

export class DialogCallbackOne implements DialogCallback {
  constructor(private onConfirmCallback: () => void) {
  }

  public onConfirm(): void {
    return this.onConfirmCallback();
  }

  public onOption(): void {
  }

  public onDeny(): void {
  }
}

export class DialogCallbackTwo implements DialogCallback {
  constructor(private onConfirmCallback: () => void, private onDenyCallback: () => void) {
  }

  public onConfirm(): void {
    return this.onConfirmCallback();
  }

  public onOption(): void {
  }

  public onDeny(): void {
    return this.onDenyCallback();
  }
}

export class DialogCallbackThree implements DialogCallback {
  constructor(private onConfirmCallback: () => void, private onOptionCallback: () => void, private onDenyCallback: () => void) {
  }

  public onConfirm(): void {
    return this.onConfirmCallback();
  }

  public onOption(): void {
    return this.onOptionCallback();
  }

  public onDeny(): void {
    return this.onDenyCallback();
  }
}