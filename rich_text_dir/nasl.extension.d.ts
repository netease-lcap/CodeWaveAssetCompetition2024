declare namespace extensions.rich_text_dir.viewComponents {
  const ViewComponent: typeof nasl.ui.ViewComponent, ViewComponentOptions: typeof nasl.ui.ViewComponentOptions;
  export class RichTextDir extends ViewComponent {
    constructor(options?: Partial<RichTextDirOptions>);
  }
  export class RichTextDirOptions extends ViewComponentOptions {
    /**
     * 富文本内容
     */
    content: nasl.core.String;
    /**
     * 目录标题
     */
    title: nasl.core.String;
    /**
     * 高度
     */
    height: nasl.core.String;
    /**
     * 目录宽度
     */
    sidebarWidth: nasl.core.String;
  }
  export {};
}