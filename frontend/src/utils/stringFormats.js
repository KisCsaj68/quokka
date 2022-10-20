export const capitalize = (str) => {
    if (str === undefined || str.length <= 1) return str;
    const string = String(str).toLowerCase();
    return `${string.charAt(0).toUpperCase()}${string.slice(1)}`;
};
