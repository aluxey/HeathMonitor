import { Outlet, useLocation, useNavigate } from 'react-router';
import { Home, TrendingUp, Settings } from 'lucide-react';

export function Layout() {
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { path: '/app', icon: Home, label: 'Home' },
    { path: '/app/goals', icon: TrendingUp, label: 'Goals' },
    { path: '/app/profile', icon: Settings, label: 'Profile' },
  ];

  const isActive = (path: string) => {
    if (path === '/app') {
      return location.pathname === '/app';
    }
    return location.pathname.startsWith(path);
  };

  return (
    <div className="flex flex-col h-screen max-w-md mx-auto bg-background relative overflow-hidden">
      {/* Main content area with bottom padding for nav */}
      <div className="flex-1 overflow-y-auto pb-20">
        <Outlet />
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto border-t border-border bg-card/95 backdrop-blur-sm">
        <nav className="flex items-center justify-around h-16 px-6">
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = isActive(item.path);
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={`flex flex-col items-center justify-center gap-1 flex-1 transition-colors ${
                  active ? 'text-primary' : 'text-muted-foreground'
                }`}
              >
                <Icon className="w-6 h-6" strokeWidth={active ? 2.5 : 2} />
                <span className="text-xs">{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>
    </div>
  );
}