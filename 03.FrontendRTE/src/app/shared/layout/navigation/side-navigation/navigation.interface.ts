export interface INavigation {
  label: string;
  icon: string;
  link?: string[];
  expanded?: boolean;
  children?: INavigation[];
  enabled?: () => boolean;
}
