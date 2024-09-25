<template>
  <div ref="artPlayerContainer" style="min-width: 200px;min-height: 200px;"></div>
</template>

<script>
import Artplayer from 'artplayer';

export default {
  name: 'ArtPlayerComponent',
  props: {
    options: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      art: null,
    };
  },
  mounted() {
    this.art = new Artplayer({
      ...this.options,
      container: this.$refs.artPlayerContainer,      
    });

    this.$nextTick(() => {
        this.$emit('get-instance', this.art);
    });
  },
  beforeDestroy() {
    if (this.art && this.art.destroy) {
        this.art.destroy(false);
    }
  }
};
</script>

<style scoped>

</style>