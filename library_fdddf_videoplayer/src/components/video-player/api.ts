/// <reference types="@nasl/types" />
namespace extensions.library_fdddf_videoplayer.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, Param, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: '视频播放器',
    description: '视频播放器',
  })
  export class VideoPlayer extends ViewComponent {
    constructor(options?: Partial<VideoPlayerOptions>) {
      super();
    }

    @Method({
        title: '播放',
        description: '开始播放'
    })
    play(): void {}

    @Method({
        title: '暂停',
        description: '暂停播放'
    })
    pause(): void {}

    @Method({
        title: '修改播放地址',
        description: '修改播放地址',
    })
    updatePlayerUrl(
      @Param({
          title: 'url地址',
      })
      newUrl: nasl.core.String
    ): void {}

    @Method({
        title: '播放速度',
        description: '设置播放器播放速度'
    })
    setPlaybackRate(
      @Param({
          title: '播放速度',
      })
      newRate: nasl.core.Decimal
    ): void {}

    @Method({
        title: '设置音量',
        description: '设置音量'
    })
    setVolume(
      @Param({
          title: '音量, 0-1',
      })
      newVolume: nasl.core.Decimal
    ): void {}

    @Method({
        title: '设置视频封面',
        description: '设置视频封面'
    })
    updatePlayerPoster(
      @Param({
          title: '视频封面地址',
      })
      newPoster: nasl.core.String
    ): void {}

    @Method({
      title: '设置高亮',
      description: '设置进度条高亮数据'
    })
    updatePlayerHighlight(
      @Param({
          title: '高亮数据',
      })
      newHighlight: nasl.collection.List<{ time: nasl.core.Decimal, text: nasl.core.String }>
    ): void {}

    @Method({
      title: '设置字幕',
      description: '设置字幕'
    })
    updatePlayerSubtitle(
      @Param({
          title: '字幕地址',
      })
      newSubtitleUrl: nasl.core.String
    ): void {}

    @Method({
      title: '设置缩略图地址',
      description: '设置缩略图地址'
    })
    updatePlayerThumbnails(
      @Param({
          title: '缩略图地址',
      })
      newThumbnailsUrl: nasl.core.String,
      @Param({
          title: '缩略图数量',
      })
      newThumbnailsNumber: nasl.core.Integer,
      @Param({
          title: '缩略图列数',
      })
      newThumbnailsColumn: nasl.core.Integer
    ): void {}
  }

  export class VideoPlayerOptions extends ViewComponentOptions {
    @Prop({
      title: '视频地址',
      description: '视频地址',
      sync: true,
      setter: {
        concept: 'InputSetter'
      }
    })
    url: nasl.core.String = '';

    @Prop({
      title: '视频封面图片地址',
      description: '视频封面图片地址',
      sync: true,
      setter: {
        concept: 'InputSetter'
      }
    })
    poster: nasl.core.String = '';

    @Prop({
      title: '缩略图地址',
      description: '缩略图地址',
      sync: true,
      setter: {
        concept: 'InputSetter'
      }
    })
    thumbnails_url: nasl.core.String = '';

    @Prop({
      title: '缩略图数量',
      description: '缩略图数量',
      sync: true,
      setter: {
        concept: 'NumberInputSetter',
      }
    })
    thumbnails_number: nasl.core.Integer;

    @Prop({
      title: '缩略图列数',
      description: '缩略图列数',
      sync: true,
      setter: {
        concept: 'NumberInputSetter',
      }
    })
    thumbnails_column: nasl.core.Integer = 10;

    @Prop({
      title: '字幕地址',
      description: '字幕URL地址',
      sync: true,
      setter: {
        concept: 'InputSetter',
      }
    })
    subtitle_url: nasl.core.String;

    @Prop({
      title: '视频章节数据源',
      description: '数据源 如 [{ start: 0, end: 18, title: "One more chance" },],',
      sync: true,
      setter: {
        concept: 'InputSetter'
      }
    })
    chapters: nasl.collection.List<{ start: nasl.core.Decimal, end: nasl.core.Decimal, title: nasl.core.String }>;

    @Prop({
      title: '进度条高亮数据',
      sync: true,
      description: '数据源 如 [{ time: 18, text: "One more chance"},]',
      setter: {
        concept: 'InputSetter'
      }
    })
    highlight: nasl.collection.List<{ time: nasl.core.Decimal, text: nasl.core.String }>;
    

    @Prop({
      title: '主题颜色',
      description: '主题颜色 Hex格式',
      sync: true,
      setter: {
        concept: 'InputSetter'
      }
    })
    theme: nasl.core.String = '#23ade5';

    @Prop({
      title: '是否自动播放',
      description: '是否自动播放',
      setter: {
        concept: 'SwitchSetter',
      }
    })
    autoplay: nasl.core.Boolean;

    @Prop({
      title: '宽度',
      description: '播放器宽度, 如500px, 100vh',
      sync: true,
      setter: {
        concept: 'InputSetter',
      }
    })
    width: nasl.core.String = '600px';

    @Prop({
      title: '高度',
      description: '播放器高度, 如300px, 100vh',
      sync: true,
      setter: {
        concept: 'InputSetter',
      }
    })
    height: nasl.core.String = '300px';

    @Event({
        title: '暂停',
        description: '暂停播放时触发',
    })
    onPause: (event: void) => void;

    @Event({
        title: '播放',
        description: '点击播放时触发',
    })
    onPlay: (event: void) => void;

    @Event({
        title: '播放时间跳转',
        description: '当播放器发生时间跳转时触发',
    })
    onSeek: (event: nasl.core.Decimal) => void;

    @Event({
        title: '静音',
        description: '当静音的状态变化时触发',
    })
    onMuted: (event: nasl.core.Boolean) => void;

    @Event({
        title: '全屏',
        description: '当播放器发生窗口全屏时触发',
    })
    onFullscreen: (event: nasl.core.Boolean) => void;

    @Event({
        title: '网页全屏',
        description: '当播放器发生网页全屏时触发',
    })
    onFullscreenWeb: (event: nasl.core.Boolean) => void;

    @Event({
        title: '画中画',
        description: '当播放器进入画中画时触发',
    })
    onPip: (event: nasl.core.Boolean) => void;


  }
}