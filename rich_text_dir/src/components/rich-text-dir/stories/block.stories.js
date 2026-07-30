import Component from '../index';

export default {
  id: 'rich-text-dir-blocks',
  title: '组件列表/RichTextDir/内置区块',
  component: Component,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'padded',
  },
};

export const Default = {
  name: '基本用法',
  render: () => ({
    template: '<rich-text-dir></rich-text-dir>'
  }),
};
