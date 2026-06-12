import { Component, input } from '@angular/core';

@Component({
  selector: 'app-logo',
  templateUrl: './logo.component.html',
})
export class LogoComponent {
  readonly showText = input(true);
  readonly iconSize = input<'sm' | 'md' | 'lg'>('md');

  get sizeClass(): string {
    return {
      sm: 'w-5 h-5',
      md: 'w-6 h-6 sm:w-7 sm:h-7',
      lg: 'w-10 h-10',
    }[this.iconSize()];
  }

  get containerClass(): string {
    return {
      sm: 'p-1 rounded-lg',
      md: 'p-2 rounded-xl',
      lg: 'p-3 rounded-2xl',
    }[this.iconSize()];
  }

  get textClass(): string {
    return {
      sm: 'text-xs font-bold leading-none',
      md: 'text-base sm:text-lg font-extrabold tracking-tight',
      lg: 'text-2xl font-black tracking-tight',
    }[this.iconSize()];
  }
}
