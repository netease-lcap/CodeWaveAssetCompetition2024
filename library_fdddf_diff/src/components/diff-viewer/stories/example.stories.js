import Component from '../index';

export default {
  id: 'diff-viewer-examples',
  title: '组件列表/DiffViewer/示例',
  component: Component,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'padded',
  },
  // More on argTypes: https://storybook.js.org/docs/api/argtypes
  argTypes: {
    one: {
      control: { type: 'text' },
    },
    other: {
      control: { type: 'text' },
    },
    diffType: {
      control: { type: 'select' },
      options: ['line', 'char', 'word'],
    },
  },
};

export const Example1 = {
  name: '基本用法',
  render: (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    template: '<diff-viewer v-bind="$props"></diff-viewer>',
  }),
  args: {
    one: 'Hello world\n世界你好',
    other: 'Hello world\nHello, Great wall',
    diffType: 'line',
  },
};
