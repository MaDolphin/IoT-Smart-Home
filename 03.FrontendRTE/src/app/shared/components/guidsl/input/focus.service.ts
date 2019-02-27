/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from '@angular/core';

/**
 * This service is used to monitor focus changes such that it can be used
 * in e.g. money input to determine if the balance should be updated.
 */
@Injectable()
export class FocusService {

    /**
     * Set to false if no element is focused
     * Set to true if a element is focused
     * @type {boolean}
     * @private
     */
    private _hasFocus: boolean = false;

    constructor() {}

    /**
     *
     * @returns {boolean} whether there is an element that has focus
     */
    get hasFocus(): boolean {
        return this._hasFocus;
    }

    /**
     * Should be called if an element loses focus
     */
    public onFocusOut() {
        this._hasFocus = false;
    }

    /**
     * Should be called if an element is focused
     */
    public onFocus() {
        this._hasFocus = true;
    }
}