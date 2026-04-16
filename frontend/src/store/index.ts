/**
 * 集中导出 Pinia stores，便于业务模块按需引用、避免深层路径散落。
 */
export { useUserStore } from './user';
export { useLiveStore } from './live';
export { usePermissionStore } from './permission';
