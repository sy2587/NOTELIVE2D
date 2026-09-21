import { Mark, mergeAttributes } from '@tiptap/core'

export const TextColor = Mark.create({
  name: 'textColor',
  addAttributes() {
    return {
      color: {
        default: null,
        parseHTML: element => element.style.color || null,
        renderHTML: attributes => attributes.color ? { style: `color: ${attributes.color}` } : {}
      }
    }
  },
  parseHTML() { return [{ tag: 'span[style*="color"]' }] },
  renderHTML({ HTMLAttributes }) { return ['span', mergeAttributes(HTMLAttributes), 0] },
  addCommands() {
    return {
      setTextColor: color => ({ commands }) => commands.setMark(this.name, { color }),
      unsetTextColor: () => ({ commands }) => commands.unsetMark(this.name)
    }
  }
})
