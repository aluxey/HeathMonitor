import { ReactNode } from 'react';
import { Progress } from './ui/progress';

interface GoalCardProps {
  icon: ReactNode;
  title: string;
  current: number;
  target: number;
  unit: string;
  color?: string;
}

export function GoalCard({ icon, title, current, target, unit, color = 'bg-primary' }: GoalCardProps) {
  const percentage = Math.min((current / target) * 100, 100);
  const achieved = current >= target;

  return (
    <div className="bg-card border border-border rounded-2xl p-5">
      <div className="flex items-start gap-4 mb-4">
        <div className={`${color} rounded-xl p-2.5 text-white`}>
          {icon}
        </div>
        <div className="flex-1">
          <h3 className="font-semibold text-foreground mb-1">{title}</h3>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-foreground">{current}</span>
            <span className="text-sm text-muted-foreground">/ {target} {unit}</span>
          </div>
        </div>
      </div>
      
      <Progress value={percentage} className="h-2 mb-2" />
      
      <div className="flex items-center justify-between">
        <span className="text-xs text-muted-foreground">{Math.round(percentage)}% complete</span>
        {achieved && (
          <span className="text-xs font-medium text-success">✓ Goal achieved!</span>
        )}
      </div>
    </div>
  );
}
