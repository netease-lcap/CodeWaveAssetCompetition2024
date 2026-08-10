/// <reference types="@nasl/types" />
namespace extensions.library_fdddf_websocket.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Param, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'both',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: 'Websocket',
    description: 'Websocket',
  })
  export class Websocket extends ViewComponent {
    constructor(options?: Partial<WebsocketOptions>) {
      super();
    }

    @Method({
      title: '连接',
      description: '连接',
    })
    connect(): void {}

    @Method({
      title: '断开连接',
      description: '断开连接',
    })
    disconnect(): void {}

    @Method({
      title: '发送消息',
      description: '发送消息, json序列化字符串 如{"content":"Zhang san","userId":1}',
    })
    send(
      @Param({
        title: '消息'
      })
      body: nasl.core.String
    ): void {}
  }

  export class WebsocketOptions extends ViewComponentOptions {
    @Prop({
      title: '内容',
      description: '显示文本',
      setter: {
        concept: 'InputSetter'
      }
    })
    url: nasl.core.String = '';

    @Event({
      title: '已连接',
      description: '已连接',
    })
    onConnected: (event: void) => void

    @Event({
      title: '断开连接',
      description: '断开连接',
    })
    onDisconnected: (event: void) => void

    @Event({
      title: '收到消息',
      description: '收到消息时',
    })
    onMessage: (event: nasl.core.String) => void

    @Event({
      title: '发出消息',
      description: '发出消息时',
    })
    onSent: (event: nasl.core.String) => void

    @Event({
      title: 'websocket连接失败',
      description: 'websocket连接失败',
    })
    onError: (event: Event) => void
  
    
  }
}