import React, { useEffect, useState } from 'react';

interface RealtimeDashboardProps {
  metrics: Record<string, number>;
}

export const RealtimeDashboard: React.FC<RealtimeDashboardProps> = ({ metrics }) => {
  const [data, setData] = useState(metrics);

  useEffect(() => {
    setData(metrics);
  }, [metrics]);

  return (
    <div className="realtime-dashboard">
      <h2>实时监控大屏</h2>
      <div className="metrics-grid">
        {Object.entries(data).map(([key, value]) => (
          <div key={key} className="metric-card">
            <div className="metric-name">{key}</div>
            <div className="metric-value">{value}</div>
          </div>
        ))}
      </div>
    </div>
  );
};
