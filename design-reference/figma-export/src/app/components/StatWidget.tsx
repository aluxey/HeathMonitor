import { ReactNode } from 'react';

interface StatWidgetProps {
  icon: ReactNode;
  label: string;
  value: string;
  color?: string;
}

export function StatWidget({ icon, label, value, color = 'text-primary' }: StatWidgetProps) {
  return (
    <div className="bg-card border border-border rounded-2xl p-4 flex items-center gap-3">
      <div className={`${color} opacity-80`}>
        {icon}
      </div>
      <div className="flex-1">
        <div className="text-xs text-muted-foreground mb-0.5">{label}</div>
        <div className="text-lg font-semibold text-foreground">{value}</div>
      </div>
    </div>
  );
}
