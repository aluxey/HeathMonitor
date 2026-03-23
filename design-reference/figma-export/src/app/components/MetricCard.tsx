import { ArrowDown, ArrowUp, CloudDownload } from 'lucide-react';
import { ReactNode } from 'react';

interface MetricCardProps {
  icon: ReactNode;
  title: string;
  value: string;
  unit: string;
  trend?: {
    direction: 'up' | 'down';
    value: string;
  };
  source: 'synced' | 'manual';
  onClick?: () => void;
  color?: string;
}

export function MetricCard({
  icon,
  title,
  value,
  unit,
  trend,
  source,
  onClick,
  color = 'bg-primary',
}: MetricCardProps) {
  return (
    <button
      onClick={onClick}
      className="bg-card border border-border rounded-2xl p-5 flex flex-col gap-3 hover:shadow-md transition-shadow text-left w-full"
    >
      <div className="flex items-start justify-between">
        <div className={`${color} rounded-xl p-2.5 text-white`}>
          {icon}
        </div>
        {source === 'synced' && (
          <div className="flex items-center gap-1 text-xs text-muted-foreground">
            <CloudDownload className="w-3 h-3" />
            <span>Synced</span>
          </div>
        )}
      </div>
      
      <div>
        <div className="text-sm text-muted-foreground mb-1">{title}</div>
        <div className="flex items-baseline gap-2">
          <span className="text-3xl font-semibold text-foreground">{value}</span>
          <span className="text-sm text-muted-foreground">{unit}</span>
        </div>
      </div>

      {trend && (
        <div className="flex items-center gap-1">
          {trend.direction === 'up' ? (
            <ArrowUp className="w-4 h-4 text-success" />
          ) : (
            <ArrowDown className="w-4 h-4 text-destructive" />
          )}
          <span
            className={`text-sm ${
              trend.direction === 'up' ? 'text-success' : 'text-destructive'
            }`}
          >
            {trend.value}
          </span>
          <span className="text-xs text-muted-foreground">vs last week</span>
        </div>
      )}
    </button>
  );
}
