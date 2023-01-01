package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AnyAIDecoder extends AbstractExpandedDecoder {
   private static final int HEADER_SIZE = 5;

   AnyAIDecoder(BitArray information) {
      super(information);
   }

   public String parseInformation() throws NotFoundException {
      StringBuilder buf = new StringBuilder();
      return this.getGeneralDecoder().decodeAllCodes(buf, 5);
   }
}
