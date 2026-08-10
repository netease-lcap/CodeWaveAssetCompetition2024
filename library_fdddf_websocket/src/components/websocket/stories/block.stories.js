import Component from '../index';

export default {
  id: 'websocket-blocks',
  title: '组件列表/Websocket/内置区块',
  component: Component,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'centered',
  },
};

export const Default = {
  name: '基本用法',
  render: () => ({
    template: '<websocket url="ws://localhost:9090/ws"></websocket>',
  }),
};
