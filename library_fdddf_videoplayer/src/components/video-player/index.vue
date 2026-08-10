<template>
<div :class="$style.root">
  <ArtPlayerComponent @get-instance="getInstance" :options="mergedPlayerOptions" :style="style" />
</div>
</template>
<script>

import ArtPlayerComponent from './ArtPlayer.vue';
import artplayerPluginChapter from 'artplayer-plugin-chapter';


export default {
  name: 'video-player',
  components: {
    ArtPlayerComponent,
  },
  props: {
    url: {
      type: String,
      default: 'https://artplayer.org/assets/sample/video.mp4'
    },
    poster: {
      type: String,
      default: 'https://artplayer.org/assets/sample/poster.jpg'
    },
    theme: {
      type: String,
      default: '#23ade5'
    },
    autoplay: {
      type: Boolean,
      default: false
    },
    chapters: {
      type: Array,
      default: () => [
        { start: 0, end: 18, title: 'One more chance' },
        { start: 18, end: 36, title: '谁でもいいはずなのに' },
        { start: 36, end: 54, title: '夏の想い出がまわる' },
        { start: 54, end: 72, title: 'こんなとこにあるはずもないのに' },
        { start: 72, end: Infinity, title: '终わり' },
      ]
    },
    highlight: {
      type: Array,
      default: () => [
        { time: 15, text: 'One more chance' },
        { time: 30, text: '谁でもいいはずなのに' },
        { time: 45, text: '夏の想い出がまわる' },
        { time: 60, text: 'こんなとこにあるはずもないのに' },
        { time: 75, text: '终わり' },
      ]
    },
    width: {
      type: String,
      default: '100vw',
    },
    height: {
      type: String,
      default: '100vh',
    },
    thumbnails_url: {
      type: String,
      default: 'https://artplayer.org/assets/sample/thumbnails.png'
    },
    thumbnails_number: {
      type: Number,
      default: 45
    },
    thumbnails_column: {
      type: Number,
      default: 10
    },
    subtitle_url: {
      type: String,
      default: 'https://artplayer.org/assets/sample/subtitle.srt',
    }
  },
  emits: ['pause', 'play', 'seek', 'muted', 'fullscreen', 'fullscreenWeb', 'pip'],
  data() {
    return {
      playerInstance: null,
      defaultPlayerOptions: {
        url: 'https://artplayer.org/assets/sample/video.mp4',
        poster: 'https://artplayer.org/assets/sample/poster.jpg',
        subtitle: {},
        volume: 0.5,
        isLive: false,
        muted: false,
        autoplay: false,
        pip: true,
        autoSize: true,
        autoMini: true, 
        screenshot: false,
        setting: true,
        loop: true,
        flip: true,
        hotkey: true,
        playbackRate: true,
        aspectRatio: true,
        fullscreen: true,
        fullscreenWeb: true,
        subtitleOffset: true,
        miniProgressBar: true,
        mutex: true,
        backdrop: true,
        playsInline: true,
        autoPlayback: true,
        airplay: true,
        moreVideoAttr: {
          crossOrigin: 'anonymous',
        },
        theme: '#23ade5',
        lang: navigator.language.toLowerCase(),
        plugins: [],
        highlight: [],
      },
      style: {
        width: this.width,
        height: this.height,
        margin: '0 auto 0',
      },
    };
  },
  watch: {
    url: function(newUrl, oldUrl) {
      console.log(`URL changed from ${oldUrl} to ${newUrl}`);
      // Perform necessary actions when URL changes
      this.updatePlayerUrl(newUrl);
    },
    poster(newPoster, oldPoster) {
      console.log(`Poster changed from ${oldPoster} to ${newPoster}`);
      this.updatePlayerPoster(newPoster);
    },
    highlight(newHighlight, oldHighlight) {
      console.log('Highlight changed:', oldHighlight, '->', newHighlight);
      this.updatePlayerHighlight(newHighlight);
    },
    width(newWidth, oldWidth) {
      console.log(`Width changed from ${oldWidth} to ${newWidth}`);
      this.updatePlayerDimensions(newWidth, this.height);
    },
    height(newHeight, oldHeight) {
      console.log(`Height changed from ${oldHeight} to ${newHeight}`);
      this.updatePlayerDimensions(this.width, newHeight);
    },
    theme(newTheme, oldTheme) {
      console.log(`Theme changed from ${oldTheme} to ${newTheme}`);
      this.updatePlayerTheme(newTheme);
    },
    subtitle_url(newSubtitleUrl, oldSubtitleUrl) {
      console.log(`Subtitle URL changed from ${oldSubtitleUrl} to ${newSubtitleUrl}`);
      this.updatePlayerSubtitle(newSubtitleUrl);
    },
    thumbnails_url(newThumbnailsUrl, oldThumbnailsUrl) {
      console.log(`Thumbnails URL changed from ${oldThumbnailsUrl} to ${newThumbnailsUrl}`);
      if (this.playerInstance) {
        this.playerInstance.thumbnails.url = newThumbnailsUrl;
      }
    },
  },
  computed: {
    mergedPlayerOptions() {
      return {
        ...this.defaultPlayerOptions,
        url: this.url,
        poster: this.poster,
        autoplay: this.autoplay,
        theme: this.theme,
        highlight:  this.highlight,
        thumbnails: {
            url: this.thumbnails_url,
            number: this.thumbnails_number,
            column: this.thumbnails_column,
        },
        plugins: [
          artplayerPluginChapter({
            chapters: this.chapters
          }),
        ],
      };
    },
  },
  methods: {
    play() {
      if (this.playerInstance) {
        this.playerInstance.play();
      }
    },
    pause() {
      if (this.playerInstance) {
        this.playerInstance.pause();
      }
    },
    setPlaybackRate(newRate) {
      if (this.playerInstance) {
        this.playerInstance.playbackRate = newRate
      }
    },
    setAutoHeight() {
      if (this.playerInstance) {
        this.playerInstance.autoHeight();
      }
    },
    setAuthSize() {
      if (this.playerInstance) {
        this.playerInstance.authSize();
      }
    },
    /**
     * 设置音量
     * @param newVolume [0, 1]
     */
    setVolume(newVolume) {
      if (this.playerInstance) {
        this.playerInstance.volume = newVolume;
      }
    },
    updatePlayerUrl(newUrl) {
      if (this.playerInstance) {
        this.playerInstance.switchUrl(newUrl);
      }
    },
    updatePlayerPoster(newPoster) {
      if (this.playerInstance) {
        this.playerInstance.poster = newPoster;
      }
    },
    updatePlayerHighlight(newHighlight) {
      if (this.playerInstance) {
        this.playerInstance.highlight = newHighlight;
      }
    },
    updatePlayerDimensions(newWidth, newHeight) {
      this.style.width = newWidth;
      this.style.height = newHeight;
    },
    updatePlayerTheme(newTheme) {
      if (this.playerInstance) {
        this.playerInstance.theme = newTheme; // Update player theme dynamically
      }
    },
    updatePlayerSubtitle(newSubtitleUrl) {
      if (this.playerInstance) {
        this.playerInstance.subtitle.url = newSubtitleUrl;
      }
    },
    updatePlayerThumbnails(newThumbnailsUrl, newThumbnailsNumber, newThumbnailsColumn) {
      if (this.playerInstance) {
        this.playerInstance.thumbnails = {
          url: newThumbnailsUrl,
          number: newThumbnailsNumber,
          column: newThumbnailsColumn
        }
      }
    },
    getInstance(art) {
      this.playerInstance = art;

      art.on('pause', () => {
        console.info('pause');
        this.$emit('pause');
      });

      art.on('play', () => {
        console.info('play');
        this.$emit('play');
      });

      art.on('seek', (currentTime) => {
        console.info('seek', currentTime);
        this.$emit('seek', currentTime);
      });

      art.on('muted', (state) => {
        console.log(state);
        this.$emit('muted', state);
      });

      art.on('fullscreen', (state) => {
        console.info('fullscreen', state);
        this.$emit('fullscreen', state);
      });

      art.on('fullscreenWeb', (state) => {
        console.info('fullscreenWeb', state);
        this.$emit('fullscreenWeb', state);
      });

      art.on('pip', (state) => {
        console.info('pip', state);
        this.$emit('pip', state);
      });

      art.on('ready', () => {
        this.playerInstance.subtitle.url = this.subtitle_url;
        this.playerInstance.thumbnails = {
          url: this.thumbnails_url,
          number: this.thumbnails_number,
          column: this.thumbnails_column
        }
        console.log(this.subtitle_url, this.thumbnails_url)
      })
    },
  },
  mounted() {
  }
};
</script>
<style module>
.root {
  display: flex;
  overflow: hidden;
  width: 100vw;
  height: 100vh;
  justify-content: center;
  flex-direction: column;
  margin: 0 auto 0;
}
</style>
