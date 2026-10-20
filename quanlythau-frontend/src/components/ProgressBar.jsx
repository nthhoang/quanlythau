export default function ProgressBar({ percent }) {
  const value = Math.max(0, Math.min(100, percent ?? 0));
  const ticks = Array.from({ length: 9 }, (_, i) => (i + 1) * 10);

  return (
    <div className="w-full">
      <div className="relative h-3 w-full rounded-none bg-concrete-200 overflow-hidden border border-blueprint-900/10">
        <div
          className="h-full bg-amber-500 transition-all duration-500"
          style={{ width: `${value}%` }}
        />
        <div className="absolute inset-0 flex items-center justify-between px-[1px]">
          {ticks.map((t) => (
            <span key={t} className="w-px h-full bg-concrete-50/70" />
          ))}
        </div>
      </div>
      <div className="flex justify-between mt-1">
        <span className="text-xs text-blueprint-900/60 font-display font-bold">{value}%</span>
        <span className="text-xs text-blueprint-900/40">hoàn thành</span>
      </div>
    </div>
  );
}
