/// <reference types="@nasl/types" />
namespace extensions.rich_text_dir.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    },
  })
  @Component({
    title: '富文本目录',
    description: '富文本目录',
  })
  export class RichTextDir extends ViewComponent {
    constructor(options?: Partial<RichTextDirOptions>) {
      super();
    }
  }

  export class RichTextDirOptions extends ViewComponentOptions {
    @Prop({
      title: '富文本内容',
      setter: {
        concept: 'InputSetter',
      },
    })
    content: nasl.core.String = '';
    @Prop({
      title: '目录标题',
      setter: {
        concept: 'InputSetter',
      },
    })
    title: nasl.core.String = '目录';
    @Prop({
      title: '高度',
      setter: {
        concept: 'InputSetter',
      },
    })
    height: nasl.core.String = '500px';
    @Prop({
      title: '目录宽度',
      setter: {
        concept: 'InputSetter',
      },
    })
    sidebarWidth: nasl.core.String = '240px';
  }
}
