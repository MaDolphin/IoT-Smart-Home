/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { config } from '../config';

/**
 * ### .isProxyEnabled()
 *
 * Helper function to check if Chai's proxy protection feature is enabled. If
 * proxies are unsupported or disabled via the user's Chai config, then return
 * false. Otherwise, return true.
 *
 *
 * @name isProxyEnabled
 */
export function isProxyEnabled() {
  return config.useProxy &&
    typeof Proxy !== 'undefined' &&
    typeof Reflect !== 'undefined';
}
