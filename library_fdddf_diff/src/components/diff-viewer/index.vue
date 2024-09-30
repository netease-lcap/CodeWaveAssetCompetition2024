<template>
<div :class="$style.root">
  <pre ref="result">
  </pre>
</div>
</template>
<script>
import { diffLines, diffChars, diffWords } from 'diff';

export default {
  name: 'diffResult-viewer',
  props: {
    one: {
      type: String,
      default: 'HelloWorld\n今天是个好天气\nWorld'
    },
    other: {
      type: String,
      default: 'HelloWorld\nWorld\n今天是个好天气'
    },
    diffType: {
      type: String,
      default: 'line', // line, char, word
    },
    insBackgroundColor: {
      type: String,
      default: '#d4fcbc' // Default green background for added lines
    },
    insColor: {
      type: String,
      default: '#000' // Default text color for added lines
    },
    delBackgroundColor: {
      type: String,
      default: '#fbcaca' // Default red background for removed lines
    },
    delColor: {
      type: String,
      default: '#000' // Default text color for removed lines
    }
  },
  data() {
    return {
      textA: this.one,
      textB: this.other,
      myDiffType: 'line'
    };
  },
  watch: {
    one: function () {
      this.textA = this.one;
      this.changed();
    },
    other: function () {
      this.textB = this.other;
      this.changed();
    },
    diffType: function () {
      this.myDiffType = this.diffType;
      this.changed();
    },
    insBackgroundColor: function () {
      this.changed();
    },
    insColor: function () {
      this.changed();
    },
    delBackgroundColor: function () {
      this.changed();
    },
    delColor: function () {
      this.changed();
    }
  },
  methods: {
    getLineClass(part) {
      // Return a class based on addition, removal, or unchanged
      return part.added ? 'added' : part.removed ? 'removed' : 'unchanged'; // Return plain class names
    },
    changed() {
      const Diff = {
        'line': diffLines,
        'char': diffChars,
        'word': diffWords
      };
      const diffResult = Diff[this.myDiffType](this.textA, this.textB);

      const fragment = document.createDocumentFragment();
      for (let i = 0; i < diffResult.length; i++) {
        if (diffResult[i].added && diffResult[i + 1] && diffResult[i + 1].removed) {
          const swap = diffResult[i];
          diffResult[i] = diffResult[i + 1];
          diffResult[i + 1] = swap;
        }

        let node;
        if (diffResult[i].removed) {
          node = document.createElement('del');
          node.style.backgroundColor = this.delBackgroundColor;
          node.style.color = this.delColor;
          node.appendChild(document.createTextNode(diffResult[i].value));
        } else if (diffResult[i].added) {
          node = document.createElement('ins');
          node.style.backgroundColor = this.insBackgroundColor;
          node.style.color = this.insColor;
          node.appendChild(document.createTextNode(diffResult[i].value));
        } else {
          node = document.createTextNode(diffResult[i].value);
        }
        fragment.appendChild(node);
      }
      this.$refs.result.textContent = '';
      this.$refs.result.appendChild(fragment);
    }
  },
  mounted() {
    this.changed();
  }
};
</script>
<style module>
.root {
  font-family: monospace;
  display: block;
  text-align: left;
}

ins {
  text-decoration: none;
  background-color: #d4fcbc;
  color: #333;
}

del {
  text-decoration: none;
  background-color: #fbcaca;
  color: #333;
}

pre {
  background-color: white; /* Default background for unchanged lines */
}
</style>
