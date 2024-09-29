/// <reference types="@nasl/types" />
namespace extensions.wgx_xmind_viewer.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: '预览xmind文件',
    description: '预览xmind文件',
  })
  export class XmindViewer extends ViewComponent {
    constructor(options?: Partial<XmindViewerOptions>) {
      super();
    }
  }

  export class XmindViewerOptions extends ViewComponentOptions {
     @Prop({
      title: 'xmind文件地址',
      description: 'xmind文件地址',
      setter: {
        concept: 'InputSetter'
      }
    })
    fileUrl : nasl.core.String;
  }
}