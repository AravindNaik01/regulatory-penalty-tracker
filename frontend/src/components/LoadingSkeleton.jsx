import React from 'react';
import './LoadingSkeleton.css';

export const CardSkeleton = () => {
  return (
    <div className="skeleton-card">
      <div className="skeleton-title pulse"></div>
      <div className="skeleton-text pulse"></div>
      <div className="skeleton-text short pulse"></div>
      <div className="skeleton-footer pulse"></div>
    </div>
  );
};

export const TableSkeleton = ({ rows = 5 }) => {
  return (
    <div className="skeleton-table">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="skeleton-row pulse"></div>
      ))}
    </div>
  );
};
