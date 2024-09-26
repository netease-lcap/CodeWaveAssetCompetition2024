/// <reference types="@nasl/types" />
namespace extensions.richtext_dir.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: '富文本目录',
    description: '富文本目录',
  })
  export class RichtextDir extends ViewComponent {
    constructor(options?: Partial<RichtextDirOptions>) {
      super();
    }
  }

  export class RichtextDirOptions extends ViewComponentOptions {
     @Prop({
      title: '内容',
      description: '显示文本',
      setter: {
        concept: 'InputSetter'
      }
    })
    text: nasl.core.String = '';
  }
}