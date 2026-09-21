import { Mark, mergeAttributes } from '@tiptap/core'

export const Highlight = Mark.create({
  name: 'highlight',
  addAttributes() {
    return {
      color: {
        default: null,
        parseHTML: element => element.style.backgroundColor || null,
        renderHTML: attributes => attributes.color ? { style: `background-color: ${attributes.color}` } : {}
      }
    }
  },
  parseHTML() { return [{ tag: 'mark' }, { tag: 'span[style*="background-color"]' }] },
  renderHTML({ HTMLAttributes }) { return ['mark', mergeAttributes(HTMLAttributes), 0] },
  addCommands() {
    return {
      setHighlight: color => ({ commands }) => commands.setMark(this.name, { color }),
      unsetHighlight: () => ({ commands }) => commands.unsetMark(this.name)
    }
  }
})
