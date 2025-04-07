package com.gramirez.quotes.service.impl;

import com.gramirez.quotes.repository.QuoteRepository;
import com.gramirez.quotes.service.QuotePairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuotePairServiceImpl implements QuotePairService {

    private final QuoteRepository quoteRepository;

    @Autowired
    public QuotePairServiceImpl(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    public Long countPairsWithMaxLength(int maxLength) {

        Map<Integer, Long> lengthFrequency = quoteRepository.getLengthFrequencyMap(maxLength);
        return countValidPairs(lengthFrequency, maxLength);
    }

    public long countValidPairs(Map<Integer, Long> lengthFrequency, int maxLength) {
        long totalPairs = 0;

        List<Map.Entry<Integer, Long>> entries = new ArrayList<>(lengthFrequency.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        for (Map.Entry<Integer, Long> entry : entries) {
            int length = entry.getKey();
            long freq = entry.getValue();

            if (length * 2 <= maxLength) {
                totalPairs += (freq * (freq - 1)) / 2; //combinations with text same length
            }
        }

        int n = entries.size();
        long[] prefixCounts = new long[n + 1];
        prefixCounts[0] = 0;

        for (int i = 0; i < n; i++) {
            prefixCounts[i + 1] = prefixCounts[i] + entries.get(i).getValue();
        }

        for (int i = 0; i < n; i++) {
            int currentLength = entries.get(i).getKey();
            long currentFreq = entries.get(i).getValue();

            int low = i + 1, high = n - 1, validIndex = i;
            while (low <= high) {
                int mid = low + (high - low) / 2;
                if (currentLength + entries.get(mid).getKey() <= maxLength) {
                    validIndex = mid;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            if (validIndex > i) {
                long totalFreqInRange = prefixCounts[validIndex + 1] - prefixCounts[i + 1];
                totalPairs += currentFreq * totalFreqInRange;
            }
        }

        return totalPairs;
    }

}
