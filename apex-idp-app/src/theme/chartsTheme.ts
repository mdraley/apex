import { theme as antdTheme } from 'antd';

export function useChartsTheme() {
  const { useToken } = antdTheme;
  const { token } = useToken();
  return {
    styleSheet: {
      brandColor: token.colorPrimary,
      paletteQualitative10: [
        token.colorPrimary,
        token.colorInfo,
        token.colorSuccess,
        token.colorWarning,
        token.colorError,
        token.colorTextSecondary,
        token.colorLink,
        '#8c8c8c',
        '#b37feb',
        '#13c2c2',
      ],
      axisLineBorderColor: token.colorBorder,
      axisGridBorderColor: token.colorSplit,
      labelFill: token.colorText,
    },
  };
}
