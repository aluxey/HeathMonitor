import { useNavigate } from 'react-router';
import {
  User,
  Bell,
  Shield,
  HelpCircle,
  Info,
  ChevronRight,
  CloudDownload,
  Moon,
  Database,
  Target,
} from 'lucide-react';
import { Switch } from '../components/ui/switch';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { useState } from 'react';

export function ProfileScreen() {
  const navigate = useNavigate();
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [darkModeEnabled, setDarkModeEnabled] = useState(false);

  const settingsSections = [
    {
      title: 'Health Data',
      items: [
        {
          icon: CloudDownload,
          label: 'Health Connect',
          description: '5 data sources connected',
          action: () => navigate('/health-connect'),
        },
        {
          icon: Target,
          label: 'Goals & Targets',
          description: 'Manage your health goals',
          action: () => navigate('/app/goals'),
        },
        {
          icon: Database,
          label: 'Data Storage',
          description: 'All data stored locally',
          action: () => {},
        },
      ],
    },
    {
      title: 'Preferences',
      items: [
        {
          icon: Bell,
          label: 'Notifications',
          description: 'Daily reminders and alerts',
          toggle: true,
          value: notificationsEnabled,
          onChange: setNotificationsEnabled,
        },
        {
          icon: Moon,
          label: 'Dark Mode',
          description: 'Switch to dark theme',
          toggle: true,
          value: darkModeEnabled,
          onChange: setDarkModeEnabled,
        },
      ],
    },
    {
      title: 'Support',
      items: [
        {
          icon: HelpCircle,
          label: 'Help & FAQ',
          description: 'Get help using the app',
          action: () => {},
        },
        {
          icon: Shield,
          label: 'Privacy Policy',
          description: 'How we protect your data',
          action: () => {},
        },
        {
          icon: Info,
          label: 'About',
          description: 'Version 1.0.0',
          action: () => {},
        },
      ],
    },
  ];

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 py-6">
        <h1 className="text-2xl font-bold mb-6 text-foreground">Profile</h1>
        
        {/* User info */}
        <div className="flex items-center gap-4">
          <Avatar className="w-16 h-16">
            <AvatarFallback className="bg-primary text-primary-foreground text-xl">
              JD
            </AvatarFallback>
          </Avatar>
          <div className="flex-1">
            <h2 className="text-lg font-semibold text-foreground">John Doe</h2>
            <p className="text-sm text-muted-foreground">Local account</p>
          </div>
          <button className="p-2 hover:bg-muted rounded-lg transition-colors">
            <ChevronRight className="w-5 h-5 text-muted-foreground" />
          </button>
        </div>
      </div>

      {/* Stats summary */}
      <div className="px-6 py-6">
        <div className="bg-card border border-border rounded-2xl p-5">
          <div className="grid grid-cols-3 divide-x divide-border">
            <div className="text-center px-2">
              <div className="text-2xl font-bold text-primary">87</div>
              <div className="text-xs text-muted-foreground mt-1">Avg Score</div>
            </div>
            <div className="text-center px-2">
              <div className="text-2xl font-bold text-chart-2">23</div>
              <div className="text-xs text-muted-foreground mt-1">Days Active</div>
            </div>
            <div className="text-center px-2">
              <div className="text-2xl font-bold text-chart-3">12</div>
              <div className="text-xs text-muted-foreground mt-1">Goals Met</div>
            </div>
          </div>
        </div>
      </div>

      {/* Settings sections */}
      <div className="px-6 space-y-6">
        {settingsSections.map((section, sectionIndex) => (
          <div key={sectionIndex}>
            <h3 className="text-sm font-semibold mb-3 text-muted-foreground uppercase tracking-wider">
              {section.title}
            </h3>
            <div className="bg-card border border-border rounded-2xl overflow-hidden">
              {section.items.map((item, itemIndex) => {
                const Icon = item.icon;
                const isLast = itemIndex === section.items.length - 1;

                return (
                  <div key={itemIndex}>
                    <button
                      onClick={item.action}
                      className={`w-full px-5 py-4 flex items-center gap-4 hover:bg-muted/50 transition-colors ${
                        !isLast ? 'border-b border-border' : ''
                      }`}
                    >
                      <div className="bg-primary/10 rounded-lg p-2">
                        <Icon className="w-5 h-5 text-primary" />
                      </div>
                      <div className="flex-1 text-left">
                        <div className="font-medium text-foreground">{item.label}</div>
                        <div className="text-sm text-muted-foreground">{item.description}</div>
                      </div>
                      {item.toggle ? (
                        <Switch
                          checked={item.value}
                          onCheckedChange={item.onChange}
                          onClick={(e) => e.stopPropagation()}
                        />
                      ) : (
                        <ChevronRight className="w-5 h-5 text-muted-foreground" />
                      )}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        ))}

        {/* Local-first badge */}
        <div className="bg-primary/5 border border-primary/20 rounded-2xl p-5 mt-6">
          <div className="flex gap-3">
            <div className="bg-primary/10 rounded-lg p-2 h-fit">
              <Shield className="w-5 h-5 text-primary" />
            </div>
            <div>
              <div className="font-semibold text-foreground mb-1">100% Private & Local</div>
              <div className="text-sm text-muted-foreground leading-relaxed">
                All your health data is stored exclusively on this device. No cloud sync, no external
                servers, no third-party access. You're in complete control.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}