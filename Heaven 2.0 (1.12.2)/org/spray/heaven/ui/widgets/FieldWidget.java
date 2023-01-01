package org.spray.heaven.ui.widgets;

import org.spray.heaven.font.FontRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;

public class FieldWidget {

	private FontRenderer font;

	private String fillText = "";

	private String text = "";

	private boolean borders = true;
	private boolean background = true;
	private boolean onlynumber;

	private int maxLength;

	private int color;

	private int x;
	private int y;
	private int width;
	private int height;

	private boolean obfedText;

	public int xPosition;
	public int yPosition;

	private int maxStringLength = 32;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;

	private boolean canLoseFocus = true;

	private boolean focused;

	private boolean isEnabled = true;

	private int lineScrollOffset;
	private int cursorPosition;

	private int selectionEnd;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;

	private boolean visible = true;

	public FieldWidget(FontRenderer font) {
		this.font = font;
	}

	/**
	 * Increments the cursor counter
	 */
	public void updateCursorCounter() {
		++this.cursorCounter;
	}

	/**
	 * Sets the text of the textbox, and moves the cursor to the end.
	 */
	public void setText(String textIn) {
		if (textIn.length() > this.maxStringLength) {
			this.text = textIn.substring(0, this.maxStringLength);
		} else {
			this.text = textIn;
		}

		this.setCursorPositionEnd();
	}

	/**
	 * Returns the contents of the textbox
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(i, j);
	}

	/**
	 * Adds the given text after the cursor, or replaces the currently selected text
	 * if there is a selection.
	 */
	public void writeText(String textToWrite) {
		String s = "";
		String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = this.maxStringLength - this.text.length() - (i - j);

		if (!this.text.isEmpty()) {
			s = s + this.text.substring(0, i);
		}

		int l;

		if (k < s1.length()) {
			s = s + s1.substring(0, k);
			l = k;
		} else {
			s = s + s1;
			l = s1.length();
		}

		if (!this.text.isEmpty() && j < this.text.length()) {
			s = s + this.text.substring(j);
		}

		this.text = s;
		this.moveCursorBy(i - this.selectionEnd + l);

	}

	/**
	 * Deletes the given number of words from the current cursor's position, unless
	 * there is currently a selection, in which case the selection is deleted
	 * instead.
	 */
	public void deleteWords(int num) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
			}
		}
	}

	/**
	 * Deletes the given number of characters from the current cursor's position,
	 * unless there is currently a selection, in which case the selection is deleted
	 * instead.
	 */
	public void deleteFromCursor(int num) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = num < 0;
				int i = flag ? this.cursorPosition + num : this.cursorPosition;
				int j = flag ? this.cursorPosition : this.cursorPosition + num;
				String s = "";

				if (i >= 0) {
					s = this.text.substring(0, i);
				}

				if (j < this.text.length()) {
					s = s + this.text.substring(j);
				}

				this.text = s;

				if (flag) {
					this.moveCursorBy(num);
				}

			}
		}
	}

	/**
	 * Gets the starting index of the word at the specified number of words away
	 * from the cursor position.
	 */
	public int getNthWordFromCursor(int numWords) {
		return this.getNthWordFromPos(numWords, this.getCursorPosition());
	}

	/**
	 * Gets the starting index of the word at a distance of the specified number of
	 * words away from the given position.
	 */
	public int getNthWordFromPos(int n, int pos) {
		return this.getNthWordFromPosWS(n, pos, true);
	}

	/**
	 * Like getNthWordFromPos (which wraps this), but adds option for skipping
	 * consecutive spaces
	 */
	public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		int i = pos;
		boolean flag = n < 0;
		int j = Math.abs(n);

		for (int k = 0; k < j; ++k) {
			if (!flag) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);

				if (i == -1) {
					i = l;
				} else {
					while (skipWs && i < l && this.text.charAt(i) == ' ') {
						++i;
					}
				}
			} else {
				while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ') {
					--i;
				}

				while (i > 0 && this.text.charAt(i - 1) != ' ') {
					--i;
				}
			}
		}

		return i;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the
	 * selection
	 */
	public void moveCursorBy(int num) {
		this.setCursorPosition(this.selectionEnd + num);
	}

	/**
	 * Sets the current position of the cursor.
	 */
	public void setCursorPosition(int pos) {
		this.cursorPosition = pos;
		int i = this.text.length();
		this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i);
		this.setSelectionPos(this.cursorPosition);
	}

	/**
	 * Moves the cursor to the very start of this text box.
	 */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	/**
	 * Moves the cursor to the very end of this text box.
	 */
	public void setCursorPositionEnd() {
		this.setCursorPosition(this.text.length());
	}

	/**
	 * Call this method from your GuiScreen to process the keys into the textbox
	 */
	public boolean keyTyped(char typedChar, int keyCode) {
		if (!this.focused) {
			return false;
		} else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			this.setCursorPositionEnd();
			this.setSelectionPos(0);
			return true;
		} else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(this.getSelectedText());
			return true;
		} else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			if (this.isEnabled) {
				this.writeText(GuiScreen.getClipboardString());
			}

			return true;
		} else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(this.getSelectedText());

			if (this.isEnabled) {
				this.writeText("");
			}

			return true;
		} else {
			switch (keyCode) {
			case 14:
				if (GuiScreen.isCtrlKeyDown()) {
					if (this.isEnabled) {
						this.deleteWords(-1);
					}
				} else if (this.isEnabled) {
					this.deleteFromCursor(-1);
				}

				return true;

			case 199:
				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(0);
				} else {
					this.setCursorPositionZero();
				}

				return true;

			case 203:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
					} else {
						this.setSelectionPos(this.getSelectionEnd() - 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(-1));
				} else {
					this.moveCursorBy(-1);
				}

				return true;

			case 205:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
					} else {
						this.setSelectionPos(this.getSelectionEnd() + 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(1));
				} else {
					this.moveCursorBy(1);
				}

				return true;

			case 207:
				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(this.text.length());
				} else {
					this.setCursorPositionEnd();
				}

				return true;

			case 211:
				if (GuiScreen.isCtrlKeyDown()) {
					if (this.isEnabled) {
						this.deleteWords(1);
					}
				} else if (this.isEnabled) {
					this.deleteFromCursor(1);
				}

				return true;

			default:
				if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
					if (this.isEnabled) {
						this.writeText(Character.toString(typedChar));
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * Called when mouse is clicked, regardless as to whether it is over this button
	 * or not.
	 */
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean flag = mouseX >= x && mouseX < x + this.width && mouseY >= y && mouseY < y + this.height;

		if (this.canLoseFocus) {
			this.setFocused(flag);
		}

		if (this.focused && flag && mouseButton == 0) {
			double i = mouseX - x;

			if (this.enableBackgroundDrawing) {
				i -= 4;
			}

			String s = font.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
			this.setCursorPosition(font.trimStringToWidth(s, (int) i).length() + this.lineScrollOffset);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Draws the textbox
	 */
	public void render() {
		if (this.getVisible()) {

			int i = this.isEnabled ? this.enabledColor : this.disabledColor;
			int j = this.cursorPosition - this.lineScrollOffset;
			int k = this.selectionEnd - this.lineScrollOffset;
			String s = font.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
			int l = this.enableBackgroundDrawing ? x + 4 : x;
			int i1 = (int) (y + (height - font.getFontHeight()) / 2);
			int j1 = l;

			if (k > s.length()) {
				k = s.length();
			}

			if (!s.isEmpty()) {
				j1 = (int) font.drawStringWithShadow(s, (float) l, (float) i1, i);
			}


			font.drawString("_", x + j1 / 2, i1, -1);
			
			boolean flag2 = this.text.length() >= this.getMaxStringLength();
			int k1 = j1;

			if (!flag) {
				k1 = j > 0 ? l + this.width : l;
			} else if (flag2) {
				k1 = j1 - 1;
				--j1;
			}

		}
	}

	/**
	 * Draws the current selection and a vertical line cursor in the text box.
	 */
	private void drawCursorVertical(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > x + this.width) {
			endX = x + this.width;
		}

		if (startX > x + this.width) {
			startX = x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) startX, (double) endY, 0.0D).endVertex();
		bufferbuilder.pos((double) endX, (double) endY, 0.0D).endVertex();
		bufferbuilder.pos((double) endX, (double) startY, 0.0D).endVertex();
		bufferbuilder.pos((double) startX, (double) startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Sets the maximum length for the text in this text box. If the current text is
	 * longer than this length, the current text will be trimmed.
	 */
	public void setMaxStringLength(int length) {
		this.maxStringLength = length;

		if (this.text.length() > length) {
			this.text = this.text.substring(0, length);
		}
	}

	/**
	 * returns the maximum number of character that can be contained in this textbox
	 */
	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/**
	 * Gets whether the background and outline of this text box should be drawn
	 * (true if so).
	 */
	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	/**
	 * Sets whether or not the background and outline of this text box should be
	 * drawn.
	 */
	public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
		this.enableBackgroundDrawing = enableBackgroundDrawingIn;
	}

	/**
	 * Sets the color to use when drawing this text box's text. A different color is
	 * used if this text box is disabled.
	 */
	public void setTextColor(int color) {
		this.enabledColor = color;
	}

	/**
	 * Sets the color to use for text in this text box when this text box is
	 * disabled.
	 */
	public void setDisabledTextColour(int color) {
		this.disabledColor = color;
	}

	/**
	 * Sets focus to this gui element
	 */
	public void setFocused(boolean isFocusedIn) {
		if (isFocusedIn && !this.focused) {
			this.cursorCounter = 0;
		}

		this.focused = isFocusedIn;

		if (Minecraft.getMinecraft().currentScreen != null) {
			Minecraft.getMinecraft().currentScreen.func_193975_a(isFocusedIn);
		}
	}

	/**
	 * Getter for the focused field
	 */
	public boolean isFocused() {
		return this.focused;
	}

	/**
	 * Sets whether this text box is enabled. Disabled text boxes cannot be typed
	 * in.
	 */
	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
	}

	/**
	 * the side of the selection that is not the cursor, may be the same as the
	 * cursor
	 */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/**
	 * returns the width of the textbox depending on if background drawing is
	 * enabled
	 */
	public int getWidth() {
		return (int) this.width - 6;
	}

	/**
	 * Sets the position of the selection anchor (the selection anchor and the
	 * cursor position mark the edges of the selection). If the anchor is set beyond
	 * the bounds of the current text, it will be put back inside.
	 */
	public void setSelectionPos(int position) {
		int i = this.text.length();

		if (position > i) {
			position = i;
		}

		if (position < 0) {
			position = 0;
		}

		this.selectionEnd = position;

		if (font != null) {
			if (this.lineScrollOffset > i) {
				this.lineScrollOffset = i;
			}

			int j = this.getWidth();
			String s = font.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
			int k = s.length() + this.lineScrollOffset;

			if (position == this.lineScrollOffset) {
				this.lineScrollOffset -= font.trimStringToWidth(this.text, j, true).length();
			}

			if (position > k) {
				this.lineScrollOffset += position - k;
			} else if (position <= this.lineScrollOffset) {
				this.lineScrollOffset -= this.lineScrollOffset - position;
			}

			this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
		}
	}

	/**
	 * Sets whether this text box loses focus when something other than it is
	 * clicked.
	 */
	public void setCanLoseFocus(boolean canLoseFocusIn) {
		this.canLoseFocus = canLoseFocusIn;
	}

	/**
	 * returns true if this textbox is visible
	 */
	public boolean getVisible() {
		return this.visible;
	}

	/**
	 * Sets whether or not this textbox is visible
	 */
	public void setVisible(boolean isVisible) {
		this.visible = isVisible;
	}

	/**
	 * Sets the fill text
	 *
	 * @param value text
	 */
	public void setFillText(String value) {
		fillText = value;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getColor() {
		return color;
	}
}