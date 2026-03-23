import { useState } from 'react';
import { useNavigate } from 'react-router';
import { CloudDownload, Heart, Activity, Footprints, Moon, Droplets, Check } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Checkbox } from '../components/ui/checkbox';

const permissions = [
  { id: 'steps', icon: Footprints, label: 'Steps', description: 'Daily step count' },
  { id: 'heart', icon: Heart, label: 'Heart Rate', description: 'Heart rate measurements' },
  { id: 'sleep', icon: Moon, label: 'Sleep', description: 'Sleep duration and quality' },
  { id: 'activity', icon: Activity, label: 'Activity', description: 'Exercise and workouts' },
  { id: 'hydration', icon: Droplets, label: 'Hydration', description: 'Water intake' },
];

export function HealthConnectScreen() {
  const [selectedPermissions, setSelectedPermissions] = useState<string[]>([
    'steps',
    'heart',
    'sleep',
  ]);
  const [isConnecting, setIsConnecting] = useState(false);
  const navigate = useNavigate();

  const togglePermission = (id: string) => {
    setSelectedPermissions((prev) =>
      prev.includes(id) ? prev.filter((p) => p !== id) : [...prev, id]
    );
  };

  const handleConnect = () => {
    setIsConnecting(true);
    // Simulate connection
    setTimeout(() => {
      navigate('/app');
    }, 1500);
  };

  const handleSkip = () => {
    navigate('/app');
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      {/* Header */}
      <div className="p-6 pb-0">
        <button
          onClick={handleSkip}
          className="text-sm text-muted-foreground hover:text-foreground transition-colors mb-6"
        >
          Skip for now
        </button>
        
        <div className="bg-primary/10 rounded-3xl p-6 mb-6 flex items-center justify-center">
          <CloudDownload className="w-16 h-16 text-primary" strokeWidth={1.5} />
        </div>

        <h1 className="text-3xl font-bold mb-3 text-foreground">
          Connect Health Data
        </h1>
        <p className="text-muted-foreground mb-6">
          Sync your health data from Health Connect to automatically track your wellness metrics.
        </p>
      </div>

      {/* Permissions list */}
      <div className="flex-1 overflow-y-auto px-6 pb-6">
        <div className="bg-muted/30 rounded-2xl p-4 mb-6">
          <div className="flex gap-3">
            <div className="bg-primary/10 rounded-lg p-2 h-fit">
              <Check className="w-5 h-5 text-primary" />
            </div>
            <div>
              <div className="font-medium text-foreground mb-1">Local & Secure</div>
              <div className="text-sm text-muted-foreground">
                Data syncs directly from Health Connect to this app only. Nothing is uploaded to external servers.
              </div>
            </div>
          </div>
        </div>

        <h3 className="font-semibold mb-4 text-foreground">Select data to sync</h3>
        
        <div className="space-y-3">
          {permissions.map((permission) => {
            const Icon = permission.icon;
            const isSelected = selectedPermissions.includes(permission.id);
            
            return (
              <button
                key={permission.id}
                onClick={() => togglePermission(permission.id)}
                className={`w-full bg-card border rounded-2xl p-4 flex items-center gap-4 transition-all ${
                  isSelected
                    ? 'border-primary shadow-sm'
                    : 'border-border'
                }`}
              >
                <Checkbox checked={isSelected} />
                <div className="bg-primary/10 rounded-xl p-2.5">
                  <Icon className="w-5 h-5 text-primary" />
                </div>
                <div className="flex-1 text-left">
                  <div className="font-medium text-foreground">{permission.label}</div>
                  <div className="text-xs text-muted-foreground">{permission.description}</div>
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Bottom actions */}
      <div className="p-6 pt-0 space-y-3">
        <Button
          onClick={handleConnect}
          disabled={selectedPermissions.length === 0 || isConnecting}
          className="w-full h-14 text-base rounded-xl"
        >
          {isConnecting ? 'Connecting...' : `Connect ${selectedPermissions.length} data sources`}
        </Button>
        <p className="text-xs text-center text-muted-foreground">
          You can change these permissions anytime in Settings
        </p>
      </div>
    </div>
  );
}