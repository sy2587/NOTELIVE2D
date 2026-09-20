<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'
import { TaskItem, TaskList } from '@tiptap/extension-list'

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])
const blockMenuOpen = ref(false)

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit.configure({ heading: { levels: [1, 2, 3, 4] } }),
    TaskList,
    TaskItem.configure({ nested: true }),
    Placeholder.configure({ placeholder: '用自己的話記錄理解、範例與待複習的問題…' })
  ],
  editorProps: { attributes: { class: 'tiptap-editor-content' } },
  onUpdate: ({ editor: instance }) => emit('update:modelValue', instance.getHTML())
})

watch(() => props.modelValue, value => {
  if (editor.value && value !== editor.value.getHTML()) editor.value.commands.setContent(value || '', false)
})
onBeforeUnmount(() => editor.value?.destroy())

function run(command) { command(); blockMenuOpen.value = false }
function heading(level) { run(() => editor.value.chain().focus().toggleHeading({ level }).run()) }
function isActive(name, attributes) { return editor.value?.isActive(name, attributes) }
</script>

<template>
  <div class="rich-editor" @keydown.esc="blockMenuOpen = false">
    <div class="rich-editor-toolbar" role="toolbar" aria-label="文字格式工具列">
      <div class="block-menu-wrap">
        <button type="button" class="rich-tool block-menu-trigger" :aria-expanded="blockMenuOpen" aria-controls="tiptap-block-menu" @click="blockMenuOpen = !blockMenuOpen">＋ 區塊</button>
        <div v-if="blockMenuOpen" id="tiptap-block-menu" class="tiptap-block-menu" role="menu" aria-label="基本區塊">
          <p>基本區塊</p>
          <button type="button" role="menuitem" @click="run(() => editor.chain().focus().setParagraph().run())"><strong>T</strong><span>文字</span></button>
          <button v-for="level in [1, 2, 3, 4]" :key="level" type="button" role="menuitem" @click="heading(level)"><strong>H{{ level }}</strong><span>標題 {{ level }}</span></button>
          <button type="button" role="menuitem" @click="run(() => editor.chain().focus().toggleBulletList().run())"><strong>•</strong><span>項目符號列表</span></button>
          <button type="button" role="menuitem" @click="run(() => editor.chain().focus().toggleOrderedList().run())"><strong>1.</strong><span>編號列表</span></button>
          <button type="button" role="menuitem" @click="run(() => editor.chain().focus().toggleTaskList().run())"><strong>☐</strong><span>待辦清單</span></button>
        </div>
      </div>
      <span class="toolbar-divider" aria-hidden="true"></span>
      <button type="button" class="rich-tool rich-mark" :class="{ active: isActive('bold') }" aria-label="粗體" :aria-pressed="isActive('bold')" @click="editor.chain().focus().toggleBold().run()">B</button>
      <button type="button" class="rich-tool rich-mark italic" :class="{ active: isActive('italic') }" aria-label="斜體" :aria-pressed="isActive('italic')" @click="editor.chain().focus().toggleItalic().run()">I</button>
      <button type="button" class="rich-tool rich-mark strike" :class="{ active: isActive('strike') }" aria-label="刪除線" :aria-pressed="isActive('strike')" @click="editor.chain().focus().toggleStrike().run()">S</button>
      <span class="toolbar-divider" aria-hidden="true"></span>
      <button type="button" class="rich-tool" aria-label="復原" :disabled="!editor?.can().undo()" @click="editor.chain().focus().undo().run()">↶</button>
      <button type="button" class="rich-tool" aria-label="重做" :disabled="!editor?.can().redo()" @click="editor.chain().focus().redo().run()">↷</button>
    </div>
    <EditorContent :editor="editor" />
  </div>
</template>
