import {Module} from "./types";

export async function go(action: string, data?: object) {
    await fetch(`/api/${action}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
}

export function sortModulesByWidth(arr: Module[]): Module[] {
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    if (!context) return arr;

    const vw = window.innerWidth / 100;
    context.font = `${vw}px 'Quantico'`;

    const metrics = new Map(arr.map(m => [m.id, context.measureText(m.name).width]));

    return [...arr].sort((a, b) => (metrics.get(b.id) || 0) - (metrics.get(a.id) || 0));
}